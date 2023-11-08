package com.dowon.fluma.document.exception;

public class NoSuchDocumentException extends RuntimeException {
    public NoSuchDocumentException() {
        super("찾는 문서를 다시 확인해주세요");
    }
    public NoSuchDocumentException(String s) {
        super(s);
    }
}
