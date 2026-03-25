package org.omkar.loanbackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username required")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Password required")
    @Size(min = 6, max = 72)
    String password;
}
