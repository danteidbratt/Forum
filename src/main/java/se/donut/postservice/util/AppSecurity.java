package se.donut.postservice.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppSecurity {

    private static final int SALT_LENGTH = 10;
    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789";

    public static String encryptPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(
                    String.format("%s, %s", password, salt).getBytes(StandardCharsets.UTF_8)
            );
            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateSalt() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SALT_LENGTH; i++) {
            sb.append(SALT_CHARS.charAt((int) (Math.random() * SALT_CHARS.length())));
        }
        return sb.toString();
    }
}
