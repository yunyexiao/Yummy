package com.yyx.exception;

/**
 * This exception indicates that the user's action is not permitted.
 */
public class InvalidActionException extends RuntimeException{
    public InvalidActionException(String msg) {
        super(msg);
    }
}
