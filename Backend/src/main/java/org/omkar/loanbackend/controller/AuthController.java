package org.omkar.loanbackend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.omkar.loanbackend.dto.AuthTokens;
import org.omkar.loanbackend.dto.LoginRequest;
import org.omkar.loanbackend.dto.LoginResponse;
import org.omkar.loanbackend.dto.RegisterRequest;
import org.omkar.loanbackend.response.ApiResponse;
import org.omkar.loanbackend.service.CookieService;
import org.omkar.loanbackend.service.JWTService;
import org.omkar.loanbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    private final CookieService cookieService;

    private final JWTService jwtService;

    public AuthController(UserService userService, CookieService cookieService, JWTService jwtService) {
        this.userService = userService;
        this.cookieService = cookieService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User registered successfully", null, true));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthTokens tokens = userService.verify(request);

        cookieService.addRefreshToken(response,tokens.getRefreshToken());

        return ResponseEntity.ok(new ApiResponse<>("Login successful", new LoginResponse(tokens.getAccessToken()),true));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response){

        String accessToken = jwtService.extractTokenFromHeader(request);

        userService.logoutSession(accessToken,refreshToken);

        cookieService.removeRefreshToken(response);

        return ResponseEntity.ok(new ApiResponse<>("Logout",null,true));
    }

    //"/refresh"
//    public ResponseEntity<ApiResponse<Void>> refreshToken(){
//
//    }

    //"/me"
}
