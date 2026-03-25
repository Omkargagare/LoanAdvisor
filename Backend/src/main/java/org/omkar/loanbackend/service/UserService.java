package org.omkar.loanbackend.service;

import org.omkar.loanbackend.dto.LoginRequest;
import org.omkar.loanbackend.dto.LoginResponse;
import org.omkar.loanbackend.dto.RegisterRequest;
import org.omkar.loanbackend.exception.UsernameAlreadyExistsException;
import org.omkar.loanbackend.model.Users;
import org.omkar.loanbackend.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo repo;

    private final AuthenticationManager authManager;

    private final JWTService jwtService;

    private final PasswordEncoder encoder;

    public UserService(UserRepo repo, AuthenticationManager authManager, JWTService jwtService, PasswordEncoder encoder) {
        this.repo = repo;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    public void registerUser(RegisterRequest request) {

        if (repo.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));

        repo.save(user);
    }

    public LoginResponse verify(LoginRequest request) {
        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtService.generateToken(authentication.getName());

        return new LoginResponse(token);
    }
}
