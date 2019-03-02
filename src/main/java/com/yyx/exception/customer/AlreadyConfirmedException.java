package com.yyx.exception.customer;

public class AlreadyConfirmedException extends EmailConfirmationException{
    public AlreadyConfirmedException(String email) {
        super("This email has already been confirmed: " + email);
    }
}
