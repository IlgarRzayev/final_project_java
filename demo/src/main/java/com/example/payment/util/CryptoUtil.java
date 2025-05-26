package com.example.payment.util;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;

public class CryptoUtil {

    // AES anahtarı üret
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        return generator.generateKey();
    }

    // AES anahtarını RSA public key ile şifrele
    public static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey clientPublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
        byte[] encrypted = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // AES key'i RSA private ile çöz
    public static SecretKey decryptAESKeyWithRSA(String encryptedKeyBase64, PrivateKey serverPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedKeyBase64));
        return new SecretKeySpec(decrypted, "AES");
    }

    // AES ile çöz
    public static String decryptAES(String encryptedBase64, String ivBase64, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(ivBase64));
        cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
        return new String(decrypted);
    }
}
