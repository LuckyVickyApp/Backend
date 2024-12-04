package LuckyVicky.backend.global.fcm.service;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.fcm.converter.FcmConverter;
import LuckyVicky.backend.global.fcm.dto.FcmMessage;
import LuckyVicky.backend.global.fcm.dto.FcmRequestDto;
import LuckyVicky.backend.global.s3.AmazonS3Manager;
import LuckyVicky.backend.global.util.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final ObjectMapper objectMapper;
    private final Constant constant;
    private final AmazonS3Manager amazonS3Manager;


    public void sendMessageTo(FcmRequestDto.FcmSimpleReqDto requestDTO) throws IOException {
        String message = makeMessage(requestDTO.getDeviceToken(), requestDTO.getTitle(), requestDTO.getBody());

        OkHttpClient client = new OkHttpClient();

        Request request = FcmConverter.createFcmRequest(constant.getFcmApiUrl(), getFcmAccessToken(), message);

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        log.info("fcm response: {}", response.body().string());
    }

    private String makeMessage(String deviceToken, String title, String body)
            throws com.fasterxml.jackson.core.JsonProcessingException { // JsonParseException, JsonProcessingException
        FcmMessage fcmMessage = FcmConverter.toFcmMessage(deviceToken, title, body);
        return objectMapper.writeValueAsString(fcmMessage);
    }

    // s3에 있는 Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받음
    public String getFcmAccessToken() {
        try (InputStream secretKeyStream = amazonS3Manager.getFcmSecretKeyFromS3()) {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(secretKeyStream)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.GOOGLE_REQUEST_TOKEN_ERROR);
        }
    }
}