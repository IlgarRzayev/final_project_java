package com.example.payment.dto;

public class KeyExchangeResponse {
    private String serverPublicKey;

    public KeyExchangeResponse(String serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }
}
