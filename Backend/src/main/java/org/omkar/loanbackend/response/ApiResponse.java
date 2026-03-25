package org.omkar.loanbackend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean success;
}
