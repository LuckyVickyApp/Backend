package LuckyVicky.backend.global.fcm.converter;

import LuckyVicky.backend.global.fcm.dto.FcmMessage;
import LuckyVicky.backend.global.fcm.dto.FcmRequestDto.FcmSimpleReqDto;
import com.google.common.net.HttpHeaders;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FcmConverter {

    public static FcmSimpleReqDto toFcmSimpleReqDto(String token, String title, String body) {
        return FcmSimpleReqDto.builder()
                .deviceToken(token)
                .title(title)
                .body(body)
                .build();
    }

    public static FcmMessage toFcmMessage(String targetToken, String title, String body) {
        return FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build())
                        .build())
                .validateOnly(false)
                .build();

    }

    public static Request createFcmRequest(String url, String accessToken, String message) {
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));

        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // Authorization 헤더
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8") // Content-Type 헤더
                .build();
    }

}
