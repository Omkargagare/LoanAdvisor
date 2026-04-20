package org.omkar.loanbackend.exception.custom;

public class JtiNotFoundException extends RuntimeException {
    public JtiNotFoundException(String message) {
        super(message);
    }
}
