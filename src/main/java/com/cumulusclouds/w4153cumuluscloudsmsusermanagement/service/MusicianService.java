package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Musician;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.MusicianRepository;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.AccountRepository;

@Service
@Transactional
public class MusicianService {

    @Autowired
    private MusicianRepository musicianRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Musician> getAllMusicians() {
        return musicianRepository.findAll();
    }

    public Optional<Musician> getMusicianById(UUID id) {
        return musicianRepository.findById(id);
    }

    public Musician createMusician(Musician musician, UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found for ID: " + accountId));
        
        musician.setAccount(account);
        return musicianRepository.save(musician);
    }

    public Optional<Musician> updateMusician(UUID id, Musician musicianDetails) {
        return musicianRepository.findById(id)
            .map(musician -> {
                musician.setGenre(musicianDetails.getGenre());
                musician.setInstrumentsPlayed(musicianDetails.getInstrumentsPlayed());
                musician.setYearsOfExperience(musicianDetails.getYearsOfExperience());
                musician.setSampleWorks(musicianDetails.getSampleWorks());
                musician.setAvailability(musicianDetails.getAvailability());
                return musicianRepository.save(musician);
            });
    }

    public boolean deleteMusician(UUID id) {
        if (musicianRepository.existsById(id)) {
            musicianRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
