package com.example.payment.service;

import com.example.payment.dto.EncryptedCardRequest;
import com.example.payment.entity.TokenizedCard;
import com.example.payment.repository.TokenizedCardRepository;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CardService {

    @Autowired
    private TokenizedCardRepository tokenizedCardRepository;

    private final Map<String, SecretKey> sessionStore = new ConcurrentHashMap<>();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public Map<String, SecretKey> getSessionStore() {
        return sessionStore;
    }

    public String processEncryptedCard(EncryptedCardRequest request) throws Exception {
        boolean validSignature = verifySignature(
            request.getPublicKey(),
            request.getEncryptedCard(),
            request.getSignature()
        );

        if (!validSignature) {
            return "Geçersiz imza!";
        }

        SecretKey aesKey = sessionStore.get(request.getClientPublicKey());
        if (aesKey == null) {
            return "Oturum anahtarı bulunamadı!";
        }

        String decryptedJson = decryptAES(request.getEncryptedCard(), request.getIv(), aesKey);

        ObjectMapper mapper = new ObjectMapper();
        CardData cardData = mapper.readValue(decryptedJson, CardData.class);

        String token = UUID.randomUUID().toString();

        TokenizedCard card = new TokenizedCard();
        card.setToken(token);
        card.setMaskedCardData(maskCard(cardData.getCard()));
        card.setUserId(1L);  // Gerçek uygulamada oturumdan alınmalı

        tokenizedCardRepository.save(card);

        return "Kart başarıyla kaydedildi, token: " + token;
    }

    private boolean verifySignature(String publicKeyBase64, String dataBase64, String signatureBase64) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        byte[] dataBytes = Base64.getDecoder().decode(dataBase64);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519", "BC");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Signature sig = Signature.getInstance("Ed25519", "BC");
        sig.initVerify(publicKey);
        sig.update(dataBytes);

        return sig.verify(signatureBytes);
    }

    private String decryptAES(String encryptedBase64, String ivBase64, SecretKey aesKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private String maskCard(String cardNumber) {
        if (cardNumber.length() < 10) return cardNumber;

        String first6 = cardNumber.substring(0, 6);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        StringBuilder masked = new StringBuilder(first6);
        for (int i = 0; i < cardNumber.length() - 10; i++) {
            masked.append("*");
        }
        masked.append(last4);
        return masked.toString();
    }

    static class CardData {
        private String card;
        private String expiry;
        private String cvv;

        public String getCard() { return card; }
        public void setCard(String card) { this.card = card; }
        public String getExpiry() { return expiry; }
        public void setExpiry(String expiry) { this.expiry = expiry; }
        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }
    }
}
