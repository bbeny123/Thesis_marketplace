package kwasilewski.marketplace.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAUtil {

    public static String shaEncode(String email, String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        String combinedPassword = email + password;
        byte[] result = messageDigest.digest(combinedPassword.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte aResult : result) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
