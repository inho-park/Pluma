package com.dowon.fluma.version.exception;

public class NoVersionException extends RuntimeException {
    public NoVersionException(Long versionId) {
        super("no version : " + versionId);
    }
}
