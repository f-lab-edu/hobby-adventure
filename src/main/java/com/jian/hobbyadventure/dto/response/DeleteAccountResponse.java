package com.jian.hobbyadventure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회원탈퇴 응답")
@Getter
@AllArgsConstructor
public class DeleteAccountResponse {

    @Schema(description = "탈퇴 성공 여부", example = "true")
    private final boolean success;
}
