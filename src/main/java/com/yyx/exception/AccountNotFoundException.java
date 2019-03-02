package com.yyx.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String accountId) {
        super("Account " + accountId + " not found.");
    }
}
