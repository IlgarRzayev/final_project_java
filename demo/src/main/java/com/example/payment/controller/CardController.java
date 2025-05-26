package com.example.payment.controller;

import com.example.payment.dto.EncryptedCardRequest;
import com.example.payment.dto.KeyExchangeRequest;
import com.example.payment.dto.KeyExchangeResponse;
import com.example.payment.service.CardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

import java.security.spec.X509EncodedKeySpec;
import java.util.Map;


@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    // Session store: Client public key'ine göre AES key eşle
    private final ConcurrentHashMap<String, SecretKey> sessionStore = new ConcurrentHashMap<>();

    @PostMapping("/submit-card-ed25519")
    public ResponseEntity<String> submitCard(@RequestBody EncryptedCardRequest request) {
        try {
            String result = cardService.processEncryptedCard(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Sunucu hatası: " + e.getMessage());
        }
    }

    @PostMapping("/request-aes-key")
public ResponseEntity<KeyExchangeResponse> requestAesKey(@RequestBody KeyExchangeRequest request) throws Exception {
    byte[] clientPubKeyRaw = Base64.getDecoder().decode(request.getClientPublicKey());

    KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519");
    KeyPair serverKeyPair = kpg.generateKeyPair();

    KeyFactory kf = KeyFactory.getInstance("X25519");
    PublicKey clientPublicKey = kf.generatePublic(new X509EncodedKeySpec(createX509FromRawX25519(clientPubKeyRaw)));

    KeyAgreement agreement = KeyAgreement.getInstance("X25519");
    agreement.init(serverKeyPair.getPrivate());
    agreement.doPhase(clientPublicKey, true);
    byte[] sharedSecret = agreement.generateSecret();

    SecretKey aesKey = new SecretKeySpec(sharedSecret, 0, 32, "AES");

    // 🔧 Burada controller yerine service içindeki sessionStore kullanılıyor:
    cardService.getSessionStore().put(request.getClientPublicKey(), aesKey);

    byte[] serverPubKeyRaw = extractRawFromX509(serverKeyPair.getPublic().getEncoded());
    String serverPubKeyBase64 = Base64.getEncoder().encodeToString(serverPubKeyRaw);

    return ResponseEntity.ok(new KeyExchangeResponse(serverPubKeyBase64));
}


    // Client’ın gönderdiği raw 32-byte X25519 key’i X.509’a çevirir
    private byte[] createX509FromRawX25519(byte[] raw) {
        byte[] prefix = {
            0x30, 0x2A,
            0x30, 0x05,
            0x06, 0x03, 0x2B, 0x65, 0x6E,
            0x03, 0x21, 0x00
        };
        byte[] x509 = new byte[prefix.length + raw.length];
        System.arraycopy(prefix, 0, x509, 0, prefix.length);
        System.arraycopy(raw, 0, x509, prefix.length, raw.length);
        return x509;
    }

    // X.509 formatındaki 44 byte’lık public key’ten sadece raw 32 byte’ı alır
    private byte[] extractRawFromX509(byte[] x509Encoded) {
        if (x509Encoded.length == 44) {
            return java.util.Arrays.copyOfRange(x509Encoded, 12, 44);
        }
        return x509Encoded;
    }



}
