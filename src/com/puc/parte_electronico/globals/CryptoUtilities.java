package com.puc.parte_electronico.globals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jose on 5/14/14.
 */
public class CryptoUtilities {

    /**
     * Computes a SHA 256 hash for a given string.
     * @param password The string to hash.
     * @return A 256 SHA 256 of the given string.
     */
    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes());
            byte[] bytes = digest.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Should never happen
            e.printStackTrace();
            return null;
        }
    }
}
