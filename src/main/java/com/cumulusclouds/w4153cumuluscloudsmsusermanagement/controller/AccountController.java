package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.net.URL;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Account;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Booker;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Musician;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.service.AccountService;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.BookerRepository;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.repository.MusicianRepository;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Value("${gcs.bucket.name}")
    private String bucketName;
    
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Autowired
    private AccountService accountService;

    @Autowired
    private BookerRepository bookerRepository;

    @Autowired
    private MusicianRepository musicianRepository;

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
        EntityModel<Account> resource = EntityModel.of(savedAccount);
        resource.add(linkTo(methodOn(AccountController.class).getAccountById(savedAccount.getUserId())).withSelfRel());
        resource.add(linkTo(methodOn(AccountController.class).getAllAccounts()).withRel("accounts"));
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Retrieve bookers for an account", description = "Fetches a list of bookers associated with the specified account ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of bookers", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booker.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @GetMapping("/{id}/bookers")
    public ResponseEntity<List<Booker>> getBookersForAccount(@PathVariable UUID id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Booker> bookers = bookerRepository.findByAccount(accountOptional.get());
        return ResponseEntity.ok(bookers);
    }

    @Operation(summary = "Retrieve musicians for an account", description = "Fetches a list of musicians associated with the specified account ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of musicians", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Musician.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @GetMapping("/{id}/musicians")
    public ResponseEntity<List<Musician>> getMusiciansForAccount(@PathVariable UUID id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Musician> musicians = musicianRepository.findByAccount(accountOptional.get());
        return ResponseEntity.ok(musicians);
    }

    @Operation(summary = "Upload a profile picture", description = "Uploads a profile picture for the specified account ID.")
    @ApiResponse(responseCode = "200", description = "Profile picture uploaded successfully")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "500", description = "Failed to upload profile picture")
    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        Optional<Account> accountOptional = accountService.getAccountById(id);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        try {
            String fileName = "profile_pictures/" + id + "_" + file.getOriginalFilename();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                    .setContentType(file.getContentType())
                    .build();
            storage.create(blobInfo, file.getBytes());

            String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

            Account account = accountOptional.get();
            account.setProfileImageUrl(publicUrl);
            accountService.updateAccount(account);

            return ResponseEntity.ok("Profile picture uploaded successfully: " + publicUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture");
        }
    }

    @Operation(summary = "Retrieve profile picture URL", description = "Fetches the URL of the profile picture for the specified account ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the profile picture URL")
    @ApiResponse(responseCode = "404", description = "Account not found or no profile picture found")
    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<String> getProfilePicture(@PathVariable UUID id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        String imageUrl = accountOptional.get().getProfileImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile picture found");
        }

        return ResponseEntity.ok(imageUrl);
    }

    @Operation(summary = "Generate a signed URL for profile picture", description = "Generates a signed URL for temporary access to the profile picture of the specified account ID.")
    @ApiResponse(responseCode = "200", description = "Successfully generated the signed URL")
    @ApiResponse(responseCode = "404", description = "Account not found or no profile picture found")
    @ApiResponse(responseCode = "500", description = "Failed to generate signed URL")
    @GetMapping("/{id}/profile-picture/signed-url")
    public ResponseEntity<String> getSignedProfilePictureUrl(@PathVariable UUID id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        String imageUrl = accountOptional.get().getProfileImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile picture found");
        }

        // Generate a signed URL
        try {
            String fileName = imageUrl.replace("https://storage.googleapis.com/" + bucketName + "/", "");
            URL signedUrl = storage.signUrl(
                    BlobInfo.newBuilder(bucketName, fileName).build(),
                    15, TimeUnit.MINUTES);

            return ResponseEntity.ok(signedUrl.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate signed URL");
        }
    }
}