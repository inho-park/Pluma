package com.dowon.fluma.document.exception;

public class CustomNoSuchDocumentException extends RuntimeException {
    public CustomNoSuchDocumentException() {
        super("찾는 문서를 다시 확인해주세요");
    }
    public CustomNoSuchDocumentException(String s) {
        super(s);
    }
}
