package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.util.UUID;


public record CreateUserCommand(@TargetAggregateIdentifier UUID userID,
                                String username,
                                String email,
                                String passwordHash,
                                String role,
                                java.time.Instant createdAt,
                                java.time.Instant updatedAt) {

}