package com.h2rd.refactoring.usermanagement.exception.email;

public class EmailEmptyOrNullException extends EmailException {
    public EmailEmptyOrNullException(String message) {
        super(message);
    }
}