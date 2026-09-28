package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByType(AccountType type);
}
