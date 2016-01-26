package vn.hoangphan.karafind.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by eastagile-tc on 1/26/16.
 */
public class CryptoUtils {
    private static CryptoUtils instance;
    private static final String DOWNCHARS = "rogamtzfknviewsbjydulhxqpc";
    private static final String UPCHARS = "ROGAMTZFKNVIEWSBJYDULHXQPC";

    public static void init() {
        instance = new CryptoUtils();
    }

    private Map<Character, Character> mEncryptionMap = new HashMap<>();
    private Map<Character, Character> mDecryptionMap = new HashMap<>();

    private CryptoUtils() {
        int size = DOWNCHARS.length(), shifting;
        if ((shifting = (int)PreferenceUtils.getInstance().getConfigLong(Constants.ENCRYPTED_KEY)) == 0) {
            shifting = new Random().nextInt(size - 1);
            PreferenceUtils.getInstance().saveConfig(Constants.ENCRYPTED_KEY, shifting);
        }

        char e, d;

        for (int i = 0; i < size; i++) {
            mEncryptionMap.put(e = DOWNCHARS.charAt(i), d = DOWNCHARS.charAt((i + shifting + 1) % size));
            mDecryptionMap.put(d, e);
            mEncryptionMap.put(e = UPCHARS.charAt(i), d = UPCHARS.charAt((i + shifting + 1) % size));
            mDecryptionMap.put(d, e);
        }
    }

    public String encrypt(String src) {
        return transform(src, mEncryptionMap);
    }

    public String decrypt(String src) {
        return transform(src, mDecryptionMap);
    }

    private String transform(String src, Map<Character, Character> charSet) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = src.length(); i < len; i++) {
            builder.append(charSet.get(src.charAt(i)));
        }
        return builder.toString();
    }
}
