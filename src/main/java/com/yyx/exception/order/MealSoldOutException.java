package com.yyx.exception.order;

public class MealSoldOutException extends OrderException{
    public MealSoldOutException(long mid) {
        super("The meal has been sold out: " + mid);
    }
}
