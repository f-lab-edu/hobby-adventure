const { S3Client, GetObjectCommand, PutObjectCommand } = require("@aws-sdk/client-s3");
const sharp = require("sharp");

const SIZE_WHITELIST = new Set(["list", "detail"]);
const SIZE_CONFIG = {
  list: { width: 400, height: 400, fit: "cover" },
  detail: { width: 1200, fit: "inside" },
};
const BUCKET = "hobby-adventure-483955238702-ap-northeast-2-an";
const REGION = "ap-northeast-2";

const s3 = new S3Client({ region: REGION });

exports.handler = async (event) => {
  const record = event.Records[0].cf;
  const request = record.request;
  const response = record.response;

  // ① 상태코드/요청경로 추출
  const status = response.status;
  const uri = request.uri;

  // ② 404/403 아니면 그대로 통과
  if (status !== "404" && status !== "403") {
    return response;
  }

  // ③ 요청경로에서 사이즈코드 추출 + 화이트리스트 검증
  const segments = uri.split("/");
  const sizeCode = segments[segments.length - 2];

  if (!SIZE_WHITELIST.has(sizeCode)) {
    return response;
  }

  // ④ 사이즈코드를 뺀 원본 경로 계산
  const originalSegments = segments.filter((_, index) => index !== segments.length - 2);
  const originalKey = originalSegments.join("/").replace(/^\//, "");

  // ⑤ S3에서 원본 가져오기
  const original = await s3.send(new GetObjectCommand({ Bucket: BUCKET, Key: originalKey }));
  const originalBuffer = await streamToBuffer(original.Body);

  // ⑥ 사이즈코드에 맞게 리사이즈 + WebP로 통일 (EXIF 방향 보정 포함)
  const config = SIZE_CONFIG[sizeCode];
  let resizer = sharp(originalBuffer).rotate();
  resizer =
    config.fit === "cover"
      ? resizer.resize(config.width, config.height, { fit: "cover" })
      : resizer.resize(config.width, null, { fit: "inside" });
  const resizedBuffer = await resizer.webp().toBuffer();

  // ⑦ 리사이즈 결과를 S3에 저장 (다음 요청부터는 Lambda 없이 바로 응답됨)
  const resizedKey = uri.replace(/^\//, "");
  await s3.send(
    new PutObjectCommand({
      Bucket: BUCKET,
      Key: resizedKey,
      Body: resizedBuffer,
      ContentType: "image/webp",
    })
  );

  // ⑧ CloudFront에 리사이즈된 이미지를 응답으로 반환
  response.status = "200";
  response.statusDescription = "OK";
  response.body = resizedBuffer.toString("base64");
  response.bodyEncoding = "base64";
  response.headers["content-type"] = [{ key: "Content-Type", value: "image/webp" }];

  return response;
};

function streamToBuffer(stream) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    stream.on("data", (chunk) => chunks.push(chunk));
    stream.on("end", () => resolve(Buffer.concat(chunks)));
    stream.on("error", reject);
  });
}
