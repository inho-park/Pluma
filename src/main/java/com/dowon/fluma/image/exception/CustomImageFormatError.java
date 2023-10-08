package com.dowon.fluma.image.exception;

public class CustomImageFormatError extends RuntimeException {
    CustomImageFormatError() {
        super();
    }

    public CustomImageFormatError(String message) {
        super(message);
    }
}
