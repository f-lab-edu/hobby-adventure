package com.jian.hobbyadventure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.image.upload-path}")
    private String uploadPath;

    public List<String> saveImages(Long recordId, List<MultipartFile> files) {
        List<String> relativePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String ext = extractExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            String relativePath = "records/" + recordId + "/" + filename;
            Path targetPath = Paths.get(uploadPath).resolve(relativePath);
            try {
                Files.createDirectories(targetPath.getParent());
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패: " + filename, e);
            }
            relativePaths.add(relativePath);
        }
        return relativePaths;
    }

    public void deleteImages(Long recordId) {
        Path dir = Paths.get(uploadPath).resolve("records").resolve(recordId.toString());
        if (!Files.exists(dir)) return;
        try (var stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
