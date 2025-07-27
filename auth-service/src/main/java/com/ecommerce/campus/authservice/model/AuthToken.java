package com.ecommerce.campus.authservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_token", indexes = {
        @Index(name = "ix_uq_token", columnList = "auth_token", unique = true),
        @Index(name = "ixn_expiry", columnList = "expiration_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_token_id")
    private Long tokenId;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false)
    private User user;

    @Column(name = "expiration_at", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "created_on", nullable = false)
    @Builder.Default
    private LocalDateTime createdOn = LocalDateTime.now();

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}