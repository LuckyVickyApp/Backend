package LuckyVicky.backend.invitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class InvitationRequestDto {
    @Schema(description = "InvitationRequestDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvitationReqDto {
        @Schema(description = "초대 코드")
        private String invitationCode;
    }
}
