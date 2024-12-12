package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Booker;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;

@Repository
public interface BookerRepository extends JpaRepository<Booker, UUID> {
    List<Booker> findByAccount(Account account);
}