package org.omkar.loanbackend.exception;

public class JtiNotFoundException extends RuntimeException {
    public JtiNotFoundException(String message) {
        super(message);
    }
}
