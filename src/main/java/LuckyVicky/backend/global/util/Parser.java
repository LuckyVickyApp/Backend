package LuckyVicky.backend.global.util;

import java.util.Arrays;
import java.util.List;

public class Parser {
    public static List<String> parseString(String message) {
        return Arrays.asList(message.split(","));
    }
}
