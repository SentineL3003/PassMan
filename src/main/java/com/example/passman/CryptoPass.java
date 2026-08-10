package com.example.passman;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoPass {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 256;          // бит
    private static final int ITERATIONS = 100_000;      // чем больше — тем безопаснее (и медленнее)
    private static final int SALT_LENGTH = 16;          // байт
    private static final int IV_LENGTH = 16;            // байт для AES

    // Храним ключ только в оперативной памяти на время сессии
    private static SecretKey currentKey = null;

    public static void initKey(String master) throws Exception {
        byte[] salt = "PassManSalt425267!".getBytes(StandardCharsets.UTF_8);

        KeySpec spec = new PBEKeySpec(
                master.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        currentKey = new SecretKeySpec(keyBytes, "AES");
    }

    public static void clearKey() {
        currentKey = null;
    }

    public static boolean isReady() {
        return currentKey != null;
    }

    public static String encrypt(String pass) throws Exception {
        if (currentKey == null) {
            throw new IllegalStateException("Ключ не инициализирован");
        }

        // генерация iv
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, currentKey, ivSpec);

        byte[] encrypted = cipher.doFinal(pass.getBytes(StandardCharsets.UTF_8));

        //iv + pass
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedPass) throws Exception {
        if (currentKey == null) {
            throw new IllegalStateException("Ключ не инициализирован");
        }

        byte[] combined = Base64.getDecoder().decode(encryptedPass);

        // отделяем iv
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        byte[] encrypted = new byte[combined.length - IV_LENGTH];
        System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, currentKey, ivSpec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
