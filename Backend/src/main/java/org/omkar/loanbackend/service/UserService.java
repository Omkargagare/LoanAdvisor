package org.omkar.loanbackend.service;

import org.jspecify.annotations.NonNull;
import org.omkar.loanbackend.dto.AuthTokens;
import org.omkar.loanbackend.dto.LoginRequest;
import org.omkar.loanbackend.dto.RegisterRequest;
import org.omkar.loanbackend.exception.UsernameAlreadyExistsException;
import org.omkar.loanbackend.model.BlacklistToken;
import org.omkar.loanbackend.model.RefreshToken;
import org.omkar.loanbackend.model.Users;
import org.omkar.loanbackend.repo.BlacklistTokenRepo;
import org.omkar.loanbackend.repo.RefreshTokenRepo;
import org.omkar.loanbackend.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    private final UserRepo userRepo;

    private final BlacklistTokenRepo blacklistRepo;

    private final RefreshTokenRepo refreshTokenRepo;

    private final AuthenticationManager authManager;

    private final JWTService jwtService;

    private final PasswordEncoder encoder;

    private final TokenService tokenService;

    public UserService(UserRepo userRepo, BlacklistTokenRepo blacklistRepo, RefreshTokenRepo refreshTokenRepo, AuthenticationManager authManager, JWTService jwtService, PasswordEncoder encoder, TokenService tokenService) {
        this.userRepo = userRepo;
        this.blacklistRepo = blacklistRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    public void registerUser(@NonNull RegisterRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));

        userRepo.save(user);
    }

    public AuthTokens verify( LoginRequest request) {
        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtService.generateToken(authentication.getName());
        String refreshToken = tokenService.generateRefreshToken();

        return new AuthTokens(accessToken, refreshToken);
    }

    public void logoutSession(String accessToken, String refreshToken) {

        if (accessToken != null) {
            try{
                String jti = jwtService.extractJtiFromToken(accessToken);
                Instant expiryTime = jwtService.extractExpirationInstant(accessToken);

                blacklistRepo.save(new BlacklistToken(jti, expiryTime));
            }catch (Exception ignored){
                //Intentionally Ignored
            }
        }

        if(refreshToken != null) {
            refreshTokenRepo.findByToken(refreshToken)
                    .ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepo.save(token);
                    });
        }
    }
}
