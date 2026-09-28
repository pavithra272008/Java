package com.example.demo.repository;

import com.example.demo.model.OrderStatus;
import com.example.demo.model.SalesOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findByOrderNumber(String orderNumber);
    List<SalesOrder> findByStatus(OrderStatus status);
    List<SalesOrder> findAllByOrderByOrderDateDesc();
}
