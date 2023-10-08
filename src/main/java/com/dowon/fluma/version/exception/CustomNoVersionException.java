package com.dowon.fluma.version.exception;

public class CustomNoVersionException extends RuntimeException {
    public CustomNoVersionException(Long versionId) {
        super("no version : " + versionId);
    }
}
