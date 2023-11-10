package com.dowon.fluma.image.exception;

public class CustomImageFormatError extends RuntimeException {
    CustomImageFormatError() {
        super();
    }

    public CustomImageFormatError(String message) {
        super(message);
    }

    public CustomImageFormatError(Long id) {
        super(id + " doesn't exist in table ( id 다시 확인 )");
    }
}
