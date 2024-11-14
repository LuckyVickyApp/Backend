package LuckyVicky.backend.aes.dto;

import javax.crypto.spec.IvParameterSpec;
import lombok.AllArgsConstructor;

public class AesDto {
    @AllArgsConstructor
    public static class EncryptedData {
        private IvParameterSpec iv;
        private byte[] encryptedPhoneNumber;

        public IvParameterSpec getIv() {
            return iv;
        }

        public byte[] getEncryptedPhoneNumber() {
            return encryptedPhoneNumber;
        }
    }

    @AllArgsConstructor
    public static class EncryptedDataInByte {
        private byte[] iv;
        private byte[] encryptedPhoneNumber;

        public byte[] getIv() {
            return iv;
        }

        public byte[] getEncryptedPhoneNumber() {
            return encryptedPhoneNumber;
        }
    }

}
