package com.example.payment.repository;

import com.example.payment.entity.TokenizedCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenizedCardRepository extends JpaRepository<TokenizedCard, Long> {
}
