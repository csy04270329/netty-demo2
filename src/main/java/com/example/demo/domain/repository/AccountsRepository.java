package com.example.demo.domain.repository;

import com.example.demo.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Account,String> {
}
