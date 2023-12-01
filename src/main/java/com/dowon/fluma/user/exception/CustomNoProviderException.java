package com.dowon.fluma.user.exception;

public class CustomNoProviderException extends RuntimeException {
    public CustomNoProviderException() {
        super();
    }

    public CustomNoProviderException(String message) {
        super(message);
    }
}
