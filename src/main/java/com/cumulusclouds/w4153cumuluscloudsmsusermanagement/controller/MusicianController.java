package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Musician;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.service.MusicianService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/accounts/musicians")
public class MusicianController {

    private static final Logger log = LoggerFactory.getLogger(MusicianController.class);

    @Autowired
    private MusicianService musicianService;

    @Operation(summary = "Retrieve all musicians", description = "Fetches a list of all available musicians from the database.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of musicians", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Musician.class)))
    @GetMapping("/")
    public CompletableFuture<ResponseEntity<List<EntityModel<Musician>>>> getAllMusicians() {
        List<Musician> musicians = musicianService.getAllMusicians();
        List<EntityModel<Musician>> resources = musicians.stream()
            .map(this::toEntityModel)
            .toList();
        return CompletableFuture.completedFuture(ResponseEntity.ok(resources));
    }

    @Operation(summary = "Retrieve musician by ID", description = "Fetches a musician based on the provided musician ID.")
    @ApiResponse(responseCode = "200", description = "Musician found successfully", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Musician.class)))
    @ApiResponse(responseCode = "404", description = "Musician not found")
    @GetMapping("/getMusicianById")
    public ResponseEntity<EntityModel<Musician>> getMusicianById(@RequestParam UUID id) {
        return musicianService.getMusicianById(id)
            .map(this::toEntityModel)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new musician", description = "Creates a new musician with the provided musician details.")
    @ApiResponse(responseCode = "200", description = "Musician created successfully", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Musician.class)))
    @ApiResponse(responseCode = "400", description = "Invalid musician data provided")
    @PostMapping("/createMusician")
    public ResponseEntity<EntityModel<Musician>> createMusician(
            @RequestBody Musician musician, @RequestParam UUID accountId) {
        try {
            // // Extract user ID (accountId) from JWT token
            // String accountId = authentication.getName();

            // Log extracted user details for debugging
            log.info("Creating musician for accountId: " + accountId);
//             log.info("JWT Subject: " + authentication.getPrincipal());
// log.info("Authorities: " + authentication.getAuthorities());


            Musician savedMusician = musicianService.createMusician(musician, accountId);
            return ResponseEntity.ok(toEntityModel(savedMusician));
        } catch (IllegalArgumentException e) {
            log.error("Invalid musician data provided: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update an existing musician", description = "Updates the details of an existing musician based on the provided musician ID.")
    @ApiResponse(responseCode = "200", description = "Musician updated successfully", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Musician.class)))
    @ApiResponse(responseCode = "404", description = "Musician not found")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Musician>> updateMusician(@PathVariable UUID id, @RequestBody Musician musicianDetails) {
        return musicianService.updateMusician(id, musicianDetails)
            .map(this::toEntityModel)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a musician", description = "Deletes the musician with the specified musician ID.")
    @ApiResponse(responseCode = "204", description = "Musician deleted successfully")
    @ApiResponse(responseCode = "404", description = "Musician not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusician(@PathVariable UUID id) {
        return musicianService.deleteMusician(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    private EntityModel<Musician> toEntityModel(Musician musician) {
        return EntityModel.of(musician,
            linkTo(methodOn(MusicianController.class).getMusicianById(musician.getMusicId())).withSelfRel(),
            linkTo(methodOn(MusicianController.class).updateMusician(musician.getMusicId(), musician)).withRel("update"),
            linkTo(methodOn(MusicianController.class).deleteMusician(musician.getMusicId())).withRel("delete"));
    }
}
