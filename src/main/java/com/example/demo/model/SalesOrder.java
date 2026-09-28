package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_orders")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Contact customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sector_id")
    private BeachSector sector;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.CONFIRMED;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 250)
    private String description;

    protected SalesOrder() {}

    public SalesOrder(String orderNumber, Contact customer, BeachSector sector, LocalDate orderDate, OrderStatus status, BigDecimal totalAmount, String description) {
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.sector = sector;
        this.orderDate = orderDate;
        this.status = status != null ? status : OrderStatus.CONFIRMED;
        this.totalAmount = totalAmount;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public Contact getCustomer() { return customer; }
    public BeachSector getSector() { return sector; }
    public LocalDate getOrderDate() { return orderDate; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getDescription() { return description; }
}
