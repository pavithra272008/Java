package com.example.demo.repository;

import com.example.demo.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    List<Payment> findByInvoiceId(Long invoiceId);
    List<Payment> findAllByOrderByPaymentDateDesc();
}
