package vn.hoangphan.karafind.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by eastagile-tc on 1/26/16.
 */
public class CryptoUtils {
    private static CryptoUtils instance;

    public static void init() {
        instance = new CryptoUtils();
    }

    public static CryptoUtils getInstance() {
        return instance;
    }

    public String getDecryptedPassword(String password) {
        String result = "";
        for (int i = 0; i < password.length(); i++) {
            char chr = password.charAt(i);
            if (chr >= '0' && chr <= '9') {
                result += String.valueOf((Integer.valueOf(chr + "") + 3) % 10);
            } else {
                result += chr;
            }
        }
        return result;
    }
}
