package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContactType type;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    protected Contact() {}

    public Contact(String name, ContactType type, String email, String phone, String taxId) {
        this.name = name;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.taxId = taxId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public ContactType getType() { return type; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getTaxId() { return taxId; }
}
