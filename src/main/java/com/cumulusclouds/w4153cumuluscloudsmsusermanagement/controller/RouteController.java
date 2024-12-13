package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.service.AccountService;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.security.JwtUtils;

import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class RouteController {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "Register new user", description = "Creates a new user account")
    @ApiResponse(responseCode = "200", description = "Registration successful")
    @ApiResponse(responseCode = "400", description = "Username or email already exists")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest == null || registerRequest.getUsername() == null || registerRequest.getEmail() == null) {
            return ResponseEntity.status(400).body("{ \"message\": \"Invalid request body\" }");
        }
        // Log the request for debugging
        System.out.println("Register Request: " + registerRequest);

        if (accountService.usernameOrEmailExists(registerRequest.getUsername(), registerRequest.getEmail())) {
            return ResponseEntity.status(400).body("{ \"message\": \"Username or email already exists\" }");
        }

        Account newAccount = new Account();
        newAccount.setUsername(registerRequest.getUsername());
        newAccount.setEmail(registerRequest.getEmail());
        newAccount.setPasswordHash(registerRequest.getPassword());
        newAccount.setRole("BOOKER"); // Default role is BOOKER FOR TESTING PURPOSES...MUST CHANGE LATER

        accountService.createAccount(newAccount);

        return ResponseEntity.ok("{ \"message\": \"Registration successful\" }");
    }

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Account> optionalAccount = accountService.findByUsername(loginRequest.getUsername());

        if (optionalAccount.isEmpty()) {
            return ResponseEntity.status(401).body("{ \"message\": \"Invalid username or password\" }");
        }

        Account account = optionalAccount.get();
        if (!accountService.checkPassword(loginRequest.getPassword(), account.getPasswordHash())) {
            return ResponseEntity.status(401).body("{ \"message\": \"Invalid username or password\" }");
        }

        String token = jwtUtils.generateJwtToken(account.getUserId().toString());
        return ResponseEntity.ok("{ \"token\": \"" + token + "\", \"message\": \"Login successful\" }");
    }

    // Data class for register requests
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Data class for login requests
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
