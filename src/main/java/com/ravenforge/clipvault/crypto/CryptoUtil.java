package com.ravenforge.clipvault.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtil {

    private static final String ALGO = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 65536;
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 16;

    public static String encrypt(String value, String password) {
        try {
            byte[] salt = new byte[SALT_LEN];
            new SecureRandom().nextBytes(salt);

            SecretKeySpec key = deriveKey(password, salt);

            Cipher cipher = Cipher.getInstance(ALGO);
            byte[] iv = new byte[IV_LEN];
            new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] encrypted = cipher.doFinal(value.getBytes());

            byte[] result = new byte[salt.length + iv.length + encrypted.length];
            System.arraycopy(salt, 0, result, 0, salt.length);
            System.arraycopy(iv, 0, result, salt.length, iv.length);
            System.arraycopy(encrypted, 0, result, salt.length + iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Error to encrypt", e);
        }
    }

    public static String decrypt(String base64, String password) {
        try {
            byte[] data = Base64.getDecoder().decode(base64);

            byte[] salt = new byte[SALT_LEN];
            byte[] iv = new byte[IV_LEN];
            byte[] encrypted = new byte[data.length - SALT_LEN - IV_LEN];

            System.arraycopy(data, 0, salt, 0, SALT_LEN);
            System.arraycopy(data, SALT_LEN, iv, 0, IV_LEN);
            System.arraycopy(data, SALT_LEN + IV_LEN, encrypted, 0, encrypted.length);

            SecretKeySpec key = deriveKey(password, salt);

            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            throw new RuntimeException("Error to decrypt: invalid password or data", e);
        }
    }

    private static SecretKeySpec deriveKey(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String hash(String value) {
        try {
            byte[] salt = new byte[SALT_LEN];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), salt, ITERATIONS, KEY_SIZE);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Concatena salt + hash
            byte[] result = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, result, 0, salt.length);
            System.arraycopy(hash, 0, result, salt.length, hash.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Error in hash generation", e);
        }
    }

    public static boolean verifyHash(String value, String base64Hash) {
        try {
            byte[] data = Base64.getDecoder().decode(base64Hash);

            byte[] salt = new byte[SALT_LEN];
            byte[] storedHash = new byte[data.length - SALT_LEN];

            System.arraycopy(data, 0, salt, 0, SALT_LEN);
            System.arraycopy(data, SALT_LEN, storedHash, 0, storedHash.length);

            PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), salt, ITERATIONS, KEY_SIZE);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] newHash = factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(storedHash, newHash);
        } catch (Exception e) {
            throw new RuntimeException("Error to verify hash", e);
        }
    }
}