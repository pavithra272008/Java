package com.example.demo.repository;

import com.example.demo.model.Invoice;
import com.example.demo.model.InvoiceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByType(InvoiceType type);
    List<Invoice> findAllByOrderByInvoiceDateDesc();
}
