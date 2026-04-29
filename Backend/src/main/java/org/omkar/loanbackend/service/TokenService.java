package org.omkar.loanbackend.service;

import org.omkar.loanbackend.exception.custom.InvalidRefreshTokenException;
import org.omkar.loanbackend.model.RefreshToken;
import org.omkar.loanbackend.model.Users;
import org.omkar.loanbackend.repo.RefreshTokenRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private final RefreshTokenRepo repo;

    private final PasswordEncoder encoder;

    public TokenService(RefreshTokenRepo repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    String generateRefreshToken(Users user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString() + UUID.randomUUID());
        refreshToken.setUser(user);
        repo.save(refreshToken);

        return refreshToken.getToken();
    }

    public String generateJti() {
        return UUID.randomUUID().toString();
    }

    public String generateCsrfToken(String refreshToken){
        RefreshToken token = repo.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        String csrfToken = UUID.randomUUID().toString();
        token.setCsrfToken(encoder.encode(csrfToken));
        repo.save(token);

        return csrfToken;
    }

    public void revokeAllUserTokens(Users user) {
        List<RefreshToken> tokens = repo.findAllByUser(user);

        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setCsrfToken(null);
        });
        repo.saveAll(tokens);
    }
}
