package org.omkar.loanbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String token;

    private boolean revoked;

    private Instant createdAt;

    private Instant expiryDate;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.expiryDate = this.createdAt.plus(Duration.ofDays(7));
        this.revoked = false;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    public boolean isExpired() {
        return !expiryDate.isAfter(Instant.now());
    }
}
