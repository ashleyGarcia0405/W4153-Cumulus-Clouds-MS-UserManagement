package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.model;

import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "account")
public class Account {

  @Id
  @GeneratedValue
  @Column(name = "user_id", updatable = false, nullable = false)
  private UUID userId;

  @Column(name = "username", length = 50, nullable = false)
  private String username;

  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "role", length = 50, nullable = false)
  private String role;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private java.time.Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private java.time.Instant updatedAt;

  @Column(name = "profile_image_url", length = 255, nullable = true)
  private String profileImageUrl;

  public UUID getUserId() {
      return userId;
  }

  public void setUserId(UUID userId) {
      this.userId = userId;
  }

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

  public String getPasswordHash() {
      return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
      this.passwordHash = passwordHash;
  }

  public String getRole() {
      return role;
  }

  public void setRole(String role) {
      this.role = role;
  }

  public java.time.Instant getCreatedAt() {
      return createdAt;
  }

  public java.time.Instant getUpdatedAt() {
      return updatedAt;
  }

  public String getProfileImageUrl() {
      return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
      this.profileImageUrl = profileImageUrl;
  }

}
