package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.query;


import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.ShortUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;

@Repository
public interface UserEntityRepository extends JpaRepository<ShortUser, UUID> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);

    static long findAllUsers() {
        return 0;
    }
}
