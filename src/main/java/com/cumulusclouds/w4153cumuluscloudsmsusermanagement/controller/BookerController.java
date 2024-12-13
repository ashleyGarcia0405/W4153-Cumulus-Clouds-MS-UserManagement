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

import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model.Booker;
import com.cumulusclouds.w4153cumuluscloudsmsusermanagement.service.BookerService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/accounts/bookers")
public class BookerController {

  @Autowired
  private BookerService bookerService;

  @Operation(summary = "Retrieve all bookers", description = "Fetches a list of all available bookers from the database.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of bookers", 
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booker.class)))
  @GetMapping("/")
  public CompletableFuture<ResponseEntity<List<EntityModel<Booker>>>> getAllBookers() {
    List<Booker> bookers = bookerService.getAllBookers();
    List<EntityModel<Booker>> resources = bookers.stream()
        .map(this::toEntityModel)
        .toList();
    return CompletableFuture.completedFuture(ResponseEntity.ok(resources));
  }

  @Operation(summary = "Retrieve booker by ID", description = "Fetches a booker based on the provided booker ID.")
  @ApiResponse(responseCode = "200", description = "Booker found successfully", 
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booker.class)))
  @ApiResponse(responseCode = "404", description = "Booker not found")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Booker>> getBookerById(@PathVariable UUID id) {
    return bookerService.getBookerById(id)
        .map(this::toEntityModel)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Create a new booker", description = "Creates a new booker with the provided booker details.")
  @ApiResponse(responseCode = "200", description = "Booker created successfully", 
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booker.class)))
  @ApiResponse(responseCode = "400", description = "Invalid booker data provided")
  @PostMapping("/")
  public ResponseEntity<EntityModel<Booker>> createBooker(@RequestBody Booker booker, @RequestParam UUID accountId) {
    try {
      Booker savedBooker = bookerService.createBooker(booker, accountId);
      return ResponseEntity.ok(toEntityModel(savedBooker));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @Operation(summary = "Update an existing booker", description = "Updates the details of an existing booker based on the provided booker ID.")
  @ApiResponse(responseCode = "200", description = "Booker updated successfully", 
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booker.class)))
  @ApiResponse(responseCode = "404", description = "Booker not found")
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<Booker>> updateBooker(@PathVariable UUID id, @RequestBody Booker bookerDetails) {
    return bookerService.updateBooker(id, bookerDetails)
        .map(this::toEntityModel)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Delete a booker", description = "Deletes the booker with the specified booker ID.")
  @ApiResponse(responseCode = "204", description = "Booker deleted successfully")
  @ApiResponse(responseCode = "404", description = "Booker not found")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBooker(@PathVariable UUID id) {
    return bookerService.deleteBooker(id) 
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

  private EntityModel<Booker> toEntityModel(Booker booker) {
    return EntityModel.of(booker,
        linkTo(methodOn(BookerController.class).getBookerById(booker.getBookerId())).withSelfRel(),
        linkTo(methodOn(BookerController.class).updateBooker(booker.getBookerId(), booker)).withRel("update"),
        linkTo(methodOn(BookerController.class).deleteBooker(booker.getBookerId())).withRel("delete"));
  }
}