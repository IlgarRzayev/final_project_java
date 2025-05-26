package com.example.payment.controller;

import com.example.payment.service.X25519KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/keys")
public class X25519KeyController {

    @Autowired
    private X25519KeyService keyService;

    @GetMapping("/server-public-key")
    public Map<String, String> getServerPublicKey() {
        Map<String, String> response = new HashMap<>();
        response.put("serverPublicKey", keyService.getPublicKeyBase64());
        return response;
    }

    @PostMapping("/compute-shared-secret")
    public Map<String, String> computeSharedSecret(@RequestBody Map<String, String> body) throws Exception {
        String clientPublicKey = body.get("clientPublicKey");
        byte[] sharedSecret = keyService.calculateSharedSecret(clientPublicKey);

        // Burada sharedSecret base64 dönebilir veya doğrudan kullanılır
        Map<String, String> response = new HashMap<>();
        response.put("sharedSecret", java.util.Base64.getEncoder().encodeToString(sharedSecret));
        return response;
    }
}
