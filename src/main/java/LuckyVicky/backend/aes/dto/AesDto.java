package LuckyVicky.backend.aes.dto;

import lombok.AllArgsConstructor;

public class AesDto {
    @AllArgsConstructor
    public static class EncryptedData {
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
