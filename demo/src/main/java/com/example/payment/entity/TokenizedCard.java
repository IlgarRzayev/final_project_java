package com.example.payment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TokenizedCards")
public class TokenizedCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    /* @Column(length = 4, nullable = false)
    private String last4; */

    @Column(nullable = false)
    private String maskedCardData;

    @Column(nullable = false)
    private Long userId;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

   /*  public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    } */

    public String getMaskedCardData() {
        return maskedCardData;
    }

    public void setMaskedCardData(String maskedCardData) {
        this.maskedCardData = maskedCardData;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
