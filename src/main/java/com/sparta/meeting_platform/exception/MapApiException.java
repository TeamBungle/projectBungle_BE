package com.sparta.meeting_platform.exception;

public class MapApiException extends RuntimeException {
    private static final String msg = "입력 정보를 다시 확인해 주세요.";

    public MapApiException() {
    }

    public MapApiException(String msg) {
        super(msg);
    }
}
