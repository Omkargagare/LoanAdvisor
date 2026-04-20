package org.omkar.loanbackend.exception.custom;

public class CsrfValidationException extends RuntimeException {
    public CsrfValidationException(String message) {
        super(message);
    }
}
