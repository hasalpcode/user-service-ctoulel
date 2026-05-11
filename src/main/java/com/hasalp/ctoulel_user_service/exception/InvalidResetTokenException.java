package com.hasalp.ctoulel_user_service.exception;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException(String message) {
        super(message);
    }
}
