package com.yyx.util;

public final class LxiiUtil {
    private static final int K = 62;
    private static final char[] DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    static {
        if(DICT.length != K) {
            throw new RuntimeException("LXII Util error!");
        }
    }

    private LxiiUtil() {

    }

    public static String convert(long number, int length) {
        if(number >= ceiling(length)) {
            throw new IllegalArgumentException("Number is too big: " + number);
        }
        char[] result = new char[length];
        long n = number;
        for(int i = 0; i< length; i++) {
            result[length - i - 1] = DICT[(int)(n % K)];
            n /= K;
        }
        return new String(result);
    }

    public static long ceiling(int length) {
        long ceiling = 1;
        for(int i = 0; i < length; i++) {
            ceiling *= K;
        }
        return ceiling;
    }
}
