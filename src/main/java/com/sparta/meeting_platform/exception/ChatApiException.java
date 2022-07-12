package com.sparta.meeting_platform.exception;

public class ChatApiException extends RuntimeException {
    private static final String msg = "입력 정보를 다시 확인해 주세요.";

    public ChatApiException() {
    }

    public ChatApiException(String msg) {
        super(msg);
    }
}


