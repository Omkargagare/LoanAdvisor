package org.omkar.loanbackend.controller;

import jakarta.validation.Valid;
import org.omkar.loanbackend.dto.LoginRequest;
import org.omkar.loanbackend.dto.LoginResponse;
import org.omkar.loanbackend.dto.RegisterRequest;
import org.omkar.loanbackend.response.ApiResponse;
import org.omkar.loanbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody RegisterRequest request) {
        service.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User registered successfully", null, true));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = service.verify(request);
        return ResponseEntity.ok(new ApiResponse<>("Login successful",response,true));
    }

    //"/logout"

    //"/refresh"

    //"/me"
}
