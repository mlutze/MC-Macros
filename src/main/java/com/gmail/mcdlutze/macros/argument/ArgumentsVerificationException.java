package com.gmail.mcdlutze.macros.argument;

public class ArgumentsVerificationException extends Exception {
    public ArgumentsVerificationException(String message) {
        super(message);
    }

    public ArgumentsVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
