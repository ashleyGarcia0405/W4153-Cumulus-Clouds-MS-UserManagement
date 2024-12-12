package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.service;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Booker;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.BookerRepository;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookerService {
    @Autowired
    private BookerRepository bookerRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Booker> getAllBookers() {
        return bookerRepository.findAll();
    }

    public Optional<Booker> getBookerById(UUID id) {
        return bookerRepository.findById(id);
    }

    public Booker createBooker(Booker booker, UUID accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }
        booker.setAccount(account.get());
        return bookerRepository.save(booker);
    }

    public Optional<Booker> updateBooker(UUID id, Booker bookerDetails) {
        return bookerRepository.findById(id)
            .map(booker -> {
                booker.setOrganizationName(bookerDetails.getOrganizationName());
                booker.setPreferredGenres(bookerDetails.getPreferredGenres());
                booker.setEventType(bookerDetails.getEventType());
                booker.setBookingHistory(bookerDetails.getBookingHistory());
                return bookerRepository.save(booker);
            });
    }

    public boolean deleteBooker(UUID id) {
        if (bookerRepository.existsById(id)) {
            bookerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
