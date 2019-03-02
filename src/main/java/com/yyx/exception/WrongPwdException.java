package com.yyx.exception;

public class WrongPwdException extends RuntimeException{
    public WrongPwdException(String id, String pwd) {
        super("Wrong password for account: " + pwd + " - " + id);
    }
}
