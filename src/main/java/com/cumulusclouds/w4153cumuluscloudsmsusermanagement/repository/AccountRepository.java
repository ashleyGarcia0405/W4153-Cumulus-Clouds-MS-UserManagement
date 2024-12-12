package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
}