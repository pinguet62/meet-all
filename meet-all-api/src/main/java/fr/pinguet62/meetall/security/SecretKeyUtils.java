package fr.pinguet62.meetall.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecretKeyUtils {

    static SecretKey fromString(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    static String toString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
