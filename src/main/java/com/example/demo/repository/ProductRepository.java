package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.ProductType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(ProductType type);
    boolean existsByNameIgnoreCase(String name);
}
