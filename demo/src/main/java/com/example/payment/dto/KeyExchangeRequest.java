package com.example.payment.dto;

public class KeyExchangeRequest {
    private String clientPublicKey;

    public String getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }
}
