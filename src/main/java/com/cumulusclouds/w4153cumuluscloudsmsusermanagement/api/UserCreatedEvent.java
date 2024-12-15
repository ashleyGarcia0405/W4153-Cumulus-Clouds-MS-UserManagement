package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.UUID;

public record UserCreatedEvent (
        UUID userID,
        String username,
        String email,
        String passwordHash,
        String role,
        java.time.Instant createdAt,
        java.time.Instant updatedAt)
{

}
