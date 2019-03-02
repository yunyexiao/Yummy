package com.yyx.model;

import java.util.Arrays;
import java.util.List;

public class OrderState {
    public static final int PLACED = 0;
    public static final int PAYED = 1;
    public static final int ACCEPTED = 2;
    public static final int DELIVERING = 3;
    public static final int COMPLETED = 4;
    public static final int SETTLED = 5;
    public static final int CANCELING = 6;
    public static final int CANCELLED = 7;

    public static final List<Integer> ALL_STATES = Arrays.asList(PLACED, PAYED, ACCEPTED, DELIVERING, COMPLETED, SETTLED, CANCELING, CANCELLED);
    public static final List<Integer> CANCEL_STATES = Arrays.asList(CANCELING, CANCELLED);
    public static final List<Integer> PAY_STATES = Arrays.asList(PAYED, ACCEPTED, DELIVERING, COMPLETED, SETTLED);
    public static final List<Integer> ACCEPT_STATES = Arrays.asList(ACCEPTED, DELIVERING, COMPLETED, SETTLED);

    public static boolean isValid(List<Integer> states) {
        return states.stream().allMatch(s -> s >= 0 && s <= 7);
    }
}
