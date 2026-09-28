package com.example.demo.repository;

import com.example.demo.model.Contact;
import com.example.demo.model.ContactType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByType(ContactType type);
    boolean existsByNameIgnoreCase(String name);
}
