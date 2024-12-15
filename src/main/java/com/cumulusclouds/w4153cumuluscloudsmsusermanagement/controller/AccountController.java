package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.controller;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.CreateUserCommand;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.ShortUser;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api.UsersNamedQueries;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.codec.ServerSentEvent;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Booker;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Musician;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.service.AccountService;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.BookerRepository;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.MusicianRepository;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private BookerRepository bookerRepository;

    @Autowired
    private MusicianRepository musicianRepository;

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public AccountController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @Operation(summary = "Retrieve all accounts", description = "Fetches a list of all available accounts from the database.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of accounts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class)))
    @GetMapping("/")
    public CompletableFuture<ResponseEntity<List<Account>>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return CompletableFuture.completedFuture(ResponseEntity.ok(accounts));
    }

    @Operation(summary = "Retrieve account by ID", description = "Fetches an account based on the provided account ID.")
    @ApiResponse(responseCode = "200", description = "Account found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Account>> getAccountById(@PathVariable UUID id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            EntityModel<Account> resource = EntityModel.of(account);
            resource.add(linkTo(methodOn(AccountController.class).getAccountById(id)).withSelfRel());
            resource.add(linkTo(methodOn(AccountController.class).getAllAccounts()).withRel("accounts"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new account", description = "Creates a new account with the provided account details.")
    @ApiResponse(responseCode = "200", description = "Account created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class)))
    @ApiResponse(responseCode = "400", description = "Invalid account data provided")
    @PostMapping("/")
    public ResponseEntity<EntityModel<Account>> createAccount(@RequestBody Account account) {
        Account savedAccount = accountService.createAccount(account);
        var command = new CreateUserCommand(savedAccount.getUserId(),savedAccount.getUsername(),savedAccount.getEmail(),savedAccount.getPasswordHash(),savedAccount.getRole(),savedAccount.getCreatedAt(),savedAccount.getUpdatedAt());
        CompletableFuture<Void> future = commandGateway.send(command);
        future.join();
        EntityModel<Account> resource = EntityModel.of(savedAccount);
        resource.add(linkTo(methodOn(AccountController.class).getAccountById(savedAccount.getUserId())).withSelfRel());
        resource.add(linkTo(methodOn(AccountController.class).getAllAccounts()).withRel("accounts"));
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}/bookers")
    public ResponseEntity<List<Booker>> getBookersForAccount(@PathVariable UUID id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Booker> bookers = bookerRepository.findByAccount(accountOptional.get());
        return ResponseEntity.ok(bookers);
    }

    @GetMapping("/{id}/musicians")
    public ResponseEntity<List<Musician>> getMusiciansForAccount(@PathVariable UUID id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Musician> musicians = musicianRepository.findByAccount(accountOptional.get());
        return ResponseEntity.ok(musicians);
    }


    //API graphQL endpoint
    @GetMapping("/user") //<.>
    public CompletableFuture<List<ShortUser>> findAll() { //<.>
        return queryGateway.query( //<.>
                UsersNamedQueries.FIND_ALL, //<.>
                null, //<.>
                ResponseTypes.multipleInstancesOf(ShortUser.class) //<.>
        );
    }

}