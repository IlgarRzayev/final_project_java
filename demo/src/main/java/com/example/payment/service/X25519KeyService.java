package com.example.payment.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;
import javax.crypto.KeyAgreement;


import jakarta.annotation.PostConstruct;
import java.security.*;
import java.security.spec.NamedParameterSpec;
import java.util.HashMap;
import java.util.Map;

@Service
public class X25519KeyService {

    private KeyPair serverKeyPair;

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519", "BC");
        kpg.initialize(new NamedParameterSpec("X25519"));
        serverKeyPair = kpg.generateKeyPair();
    }

    public String getPublicKeyBase64() {
        return java.util.Base64.getEncoder().encodeToString(serverKeyPair.getPublic().getEncoded());
    }

    public KeyPair getServerKeyPair() {
        return serverKeyPair;
    }

    // Client'ın public key base64 olarak alınır,
    // Ortak secret (AES key) oluşturulur.
    public byte[] calculateSharedSecret(String clientPublicKeyBase64) throws Exception {
        byte[] clientPubBytes = java.util.Base64.getDecoder().decode(clientPublicKeyBase64);

        KeyFactory kf = KeyFactory.getInstance("X25519", "BC");
        PublicKey clientPubKey = kf.generatePublic(new java.security.spec.X509EncodedKeySpec(clientPubBytes));

        KeyAgreement ka = KeyAgreement.getInstance("X25519", "BC");
        ka.init(serverKeyPair.getPrivate());
        ka.doPhase(clientPubKey, true);

        byte[] sharedSecret = ka.generateSecret(); // 32 byte shared secret
        return sharedSecret;
    }
}
