package org.omkar.loanbackend.service;

import org.omkar.loanbackend.model.RefreshToken;
import org.omkar.loanbackend.model.Users;
import org.omkar.loanbackend.repo.RefreshTokenRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private final RefreshTokenRepo repo;

    public TokenService(RefreshTokenRepo repo) {
        this.repo = repo;
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

    public String generateCsrfToken(){return UUID.randomUUID().toString();}

    public void revokeAllUserTokens(Users user) {
        List<RefreshToken> tokens = repo.findAllByUser(user);

        tokens.forEach(token -> token.setRevoked(true));
        repo.saveAll(tokens);
    }
}
