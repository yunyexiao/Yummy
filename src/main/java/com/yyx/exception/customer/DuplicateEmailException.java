package com.yyx.exception.customer;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Duplicate email: " + email);
    }
}
