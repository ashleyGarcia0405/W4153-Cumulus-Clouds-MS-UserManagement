package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.command;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.CreateUserCommand;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.UserCreatedEvent;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.AccountRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.UUID;

@Aggregate
public class User {

    @AggregateIdentifier
    private UUID userID;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(UUID id) {
        return accountRepository.findById(id);
    }

    @CommandHandler
    public User(CreateUserCommand command) {

        apply(new UserCreatedEvent(command.userID(),command.username(),command.email(),command.passwordHash(),command.role(),command.createdAt(),command.updatedAt()));
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent event) {
    }

}
