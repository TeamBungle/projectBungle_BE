package com.sparta.meeting_platform.exception;

public class QrcodeApiException extends RuntimeException{
    private static final String msg = "입력 정보를 다시 확인해 주세요.";

    public QrcodeApiException() {
    }

    public QrcodeApiException(String msg) {
        super(msg);
    }
}

