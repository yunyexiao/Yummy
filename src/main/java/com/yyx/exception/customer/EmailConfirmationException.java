package com.yyx.exception.customer;

public class EmailConfirmationException extends RuntimeException{
    public EmailConfirmationException(String msg) {
        super(msg);
    }
}
