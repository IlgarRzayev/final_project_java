package com.example.payment.dto;

public class EncryptedCardRequest {
    private String encryptedCard;
    private String iv;
    private String signature;
    private String publicKey;
    private String encryptedAesKey; 

    // Getter ve Setter'lar
    public String getEncryptedCard() { return encryptedCard; }
    public void setEncryptedCard(String encryptedCard) { this.encryptedCard = encryptedCard; }

    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getEncryptedAesKey() { return encryptedAesKey; }  // ✅ Getter
    public void setEncryptedAesKey(String encryptedAesKey) { this.encryptedAesKey = encryptedAesKey;}


    private String clientPublicKey;

public String getClientPublicKey() {
    return clientPublicKey;
}

public void setClientPublicKey(String clientPublicKey) {
    this.clientPublicKey = clientPublicKey;
}
}
