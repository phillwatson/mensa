package com.hillayes.mensa.payment.repository;

import com.hillayes.mensa.payment.domain.PaymentAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentAuditEventRepository extends JpaRepository<PaymentAuditEvent, UUID> {
}
