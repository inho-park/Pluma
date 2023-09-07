package com.dowon.fluma.user.exception;

public class SameUsernameException extends RuntimeException{
    public SameUsernameException() {
        super();
    }

    public SameUsernameException(String message) {
        super(message);
    }
}
