package LuckyVicky.backend.global.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import java.util.UUID;

public class UuidGenerator {

    public static String generateNanoUuid() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 12);
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}