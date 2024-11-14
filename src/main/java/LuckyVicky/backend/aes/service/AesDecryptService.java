package LuckyVicky.backend.aes.service;

import static LuckyVicky.backend.global.util.Constant.AES_PHONE_NUMBER_TRANSFORMATION;

import LuckyVicky.backend.aes.dto.AesDto.EncryptedDataInByte;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AesDecryptService {

    @Value("${aes.phone-number}")
    private String phoneNumberKey;

    private SecretKeySpec generateAESKey() {
        return new SecretKeySpec(phoneNumberKey.getBytes(), "AES");
    }

    private Cipher configureCipher(String transformation, SecretKeySpec secretKey, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher;
    }

    private EncryptedDataInByte extractIVAndEncryptedPhoneNumber(String encryptedPhoneNumber) {
        String[] parts = encryptedPhoneNumber.split(":");

        byte[] ivBytes = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

        return new EncryptedDataInByte(ivBytes, encryptedBytes);
    }

    public String decryptPhoneNumber(String encryptedPhoneNumber) throws Exception {
        SecretKeySpec secretKey = generateAESKey();

        EncryptedDataInByte encryptedData = extractIVAndEncryptedPhoneNumber(encryptedPhoneNumber);
        byte[] ivBytes = encryptedData.getIv();
        byte[] encryptedPhoneNumberBytes = encryptedData.getEncryptedPhoneNumber();

        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = configureCipher(AES_PHONE_NUMBER_TRANSFORMATION, secretKey, iv);

        byte[] decryptedBytes = cipher.doFinal(encryptedPhoneNumberBytes);

        return new String(decryptedBytes);
    }
}
