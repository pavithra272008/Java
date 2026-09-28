package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 250)
    private String description;

    @Column(name = "account_code", length = 20)
    private String accountCode;

    protected Product() {}

    public Product(String name, ProductType type, BigDecimal price, String description, String accountCode) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
        this.accountCode = accountCode;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public ProductType getType() { return type; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public String getAccountCode() { return accountCode; }
}
