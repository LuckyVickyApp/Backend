package LuckyVicky.backend.aes.service;

import static LuckyVicky.backend.global.util.Constant.AES_PHONE_NUMBER_TRANSFORMATION;

import LuckyVicky.backend.aes.dto.AesDto.EncryptedData;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AesEncryptService {

    @Value("${aes.phone-number}")
    private String phoneNumberKey;

    private EncryptedData generateInitializationVector() {
        byte[] ivBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);

        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        return new EncryptedData(ivSpec, ivBytes);
    }

    private SecretKeySpec generateAESKey() {
        return new SecretKeySpec(phoneNumberKey.getBytes(), "AES");
    }

    private Cipher configureCipher(String transformation, SecretKeySpec secretKey, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher;
    }

    public String encryptPhoneNumberWithAES(String phoneNumber)
            throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {

        SecretKeySpec secretKey = generateAESKey();

        EncryptedData encryptedData = generateInitializationVector();
        IvParameterSpec iv = encryptedData.getIv();

        Cipher cipher = configureCipher(AES_PHONE_NUMBER_TRANSFORMATION, secretKey, iv);

        byte[] encryptedBytes = cipher.doFinal(phoneNumber.getBytes());

        return Base64.getEncoder().encodeToString(iv.getIV()) + ":" + Base64.getEncoder()
                .encodeToString(encryptedBytes);
    }
}
