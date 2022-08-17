package com.hillayes.mensa.payment.repository;

import com.hillayes.mensa.payment.domain.PaymentAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public interface PaymentAuditEventRepository extends JpaRepository<PaymentAuditEvent, UUID> {
}
