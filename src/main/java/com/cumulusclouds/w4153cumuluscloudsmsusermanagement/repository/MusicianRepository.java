package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Musician;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;

@Repository
public interface MusicianRepository extends JpaRepository<Musician, UUID> {
    List<Musician> findByAccount(Account account);
}
