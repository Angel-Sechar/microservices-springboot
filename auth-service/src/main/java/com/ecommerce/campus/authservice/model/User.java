package com.ecommerce.campus.authservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "auth_user", indexes = {
        @Index(name = "uq_person_person_user", columnList = "auth_username", unique = true),
        @Index(name = "uq_person_email", columnList = "email", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"roles", "refreshTokens"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "auth_userid")
    private Long userId;

    @Column(name="auth_username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name="auth_password", nullable = false, unique = true)
    private String password;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "paternal_surname", length = 50)
    private String paternalSurname;

    @Column(name = "maternal_surname", length = 50)
    private String maternalSurname;

    @Column(name ="is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Eager fetch for performance - This tells JPA to load the roles immediately when the user is loaded
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
                name = "user_role",
            joinColumns = @JoinColumn(name = "auth_userid"),
            inverseJoinColumns = @JoinColumn(name = "auth_roleid")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // Cascade delete refresh tokens when user is deleted
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled(){
        return isActive;
    }

    @Transient
    public String getFullName() {
        return String.format("%s %s %s", firstName, paternalSurname, maternalSurname);
    }

}