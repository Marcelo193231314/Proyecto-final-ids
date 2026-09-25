package com.casino.backend.repositories;

import com.casino.backend.models.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    List<PaymentCard> findByUserId(Long userId);
}