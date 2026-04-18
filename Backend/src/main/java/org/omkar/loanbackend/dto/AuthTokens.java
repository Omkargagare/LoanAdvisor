package org.omkar.loanbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokens {
    String accessToken;
    String refreshToken;
}
