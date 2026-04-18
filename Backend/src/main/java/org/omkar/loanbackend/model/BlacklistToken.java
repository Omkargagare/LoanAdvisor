package org.omkar.loanbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "token_blacklist",
        indexes = {
                @Index(name = "idx_expiry", columnList = "expiryTime")
        }
)
public class BlacklistToken {

    @Id
    private String jti;

    @Column(nullable = false)
    private Instant expiryTime;
}
