package com.temp.demo.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryption {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final String SECRET_KEY_ALGO = "AES";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static String decrypt(byte[] cipherMessage, SecretKey secret, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] plainText = cipher.doFinal(cipherMessage);
        return new String(plainText, UTF_8);
    }

    public static String decryptWithPrefixIV(byte[] cipheredText, SecretKey secret) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(cipheredText);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);
        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);
        return decrypt(cipherText, secret, iv);
    }

    private static SecretKey getSecretKeyFromString(String secretKeyString, SecretKeyType secretKeyType) {
        switch (secretKeyType) {
            case BASE64:
                byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, SECRET_KEY_ALGO);
            case HEX:
                byte[] data = hexStringToByteArray(secretKeyString);
                return new SecretKeySpec(data, SECRET_KEY_ALGO);
        }
        return null;
    }

    public static String getDecryptedString(String secretKeyString, String encryptedString, SecretKeyType secretKeyType) {
        try {
            byte[] encryptedByte = Base64.getUrlDecoder().decode(encryptedString);
            SecretKey secretKey = getSecretKeyFromString(secretKeyString, secretKeyType);
            return Encryption.decryptWithPrefixIV(encryptedByte, secretKey);
        } catch (Exception exception) {
            return "";
        }
    }

    public static String getEncryptedString(String secretKeyString, String pText, SecretKeyType secretKeyType) {
        try {
            SecretKey secretKey = getSecretKeyFromString(secretKeyString, secretKeyType);
            byte[] encryptedByte = encryptWithPrefixIV(pText.getBytes(), secretKey);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedByte);
        } catch (Exception exception) {
            return "";
        }
    }

    public static byte[] encryptWithPrefixIV(byte[] pText, SecretKey secret) throws Exception {
        byte[] iv = getUniqueOnce();
        byte[] cipherText = encrypt(pText, secret, iv);
        return ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array();
    }

    private static byte[] getUniqueOnce() {
        byte[] nonce = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private static byte[] encrypt(byte[] plainText, SecretKey secret, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        return cipher.doFinal(plainText);
    }

    private static byte[] hexStringToByteArray(String str) {
        int len = str.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
                    + Character.digit(str.charAt(i + 1), 16));
        }
        return data;
    }

    public enum SecretKeyType {
        BASE64, HEX
    }
}
