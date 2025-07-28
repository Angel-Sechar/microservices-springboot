package com.ecommerce.campus.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "refresh_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_refresh_token_token", columnNames = {"token"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_tokenid")
    private Long refreshTokenId;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)// lazy loading — on demand
    @JoinColumn(name = "auth_userid", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_user"))
    private User user;

    @Column(name = "expiration_at", nullable = false)
    private LocalDateTime expirationAt;

    @Column(name = "creation_on", nullable = false)
    @Builder.Default
    private LocalDateTime creationOn = LocalDateTime.now();

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationAt);
    }
}