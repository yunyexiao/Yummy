package com.yyx.exception.customer;

public class EmailKeyNotMatchException extends EmailConfirmationException{
    public EmailKeyNotMatchException(String email, String key) {
        super("Email-key pair not match: " + email + " - " + key);
    }
}
