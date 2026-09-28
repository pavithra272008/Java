package com.example.demo.repository;

import com.example.demo.model.OrderStatus;
import com.example.demo.model.PurchaseOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    List<PurchaseOrder> findByStatus(OrderStatus status);
    List<PurchaseOrder> findAllByOrderByOrderDateDesc();
}
