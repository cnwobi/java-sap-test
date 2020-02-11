package com.h2rd.refactoring.usermanagement.exception.user;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
