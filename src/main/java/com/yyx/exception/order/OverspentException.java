package com.yyx.exception.order;

public class OverspentException extends OrderException{
    public OverspentException(String pid) {
        super("The payment account's balance is not enough: " + pid);
    }
}
