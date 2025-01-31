package LuckyVicky.backend.sms.service;

import static LuckyVicky.backend.global.util.Constant.PHONE_NUMBER_PATTERN;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.MessageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.owner-phone-number}")
    private String senderPhoneNumber;

    public String sendVerificationCode(String recipientPhoneNumber) {
        if (!recipientPhoneNumber.matches(PHONE_NUMBER_PATTERN)) {
            throw GeneralException.of(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }

        String verificationCode = generateVerificationCode();

        // COOLSMS API 클라이언트 생성
        var messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

        Message message = new Message();
        message.setFrom(senderPhoneNumber);
        message.setTo(recipientPhoneNumber);
        message.setText("인증번호는 [" + verificationCode + "]입니다.");
        message.setType(MessageType.SMS);

        try {
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
            if (response == null) {
                throw new IllegalStateException("Response cannot be null");
            }
            System.out.println("메시지 전송 성공: " + response.getMessageId());
        } catch (Exception e) {
            System.err.println("메시지 전송 실패: " + e.getMessage());
            throw new RuntimeException("메시지 전송에 실패했습니다.");
        }

        return verificationCode;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void verifyCode(String inputCode, String correctCode) {
        if (!inputCode.equals(correctCode)) {
            throw new GeneralException(ErrorCode.SMS_CODE_NOT_VERIFIED);
        }
    }
}

