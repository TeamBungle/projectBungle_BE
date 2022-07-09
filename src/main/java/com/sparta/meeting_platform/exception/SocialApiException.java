package com.sparta.meeting_platform.exception;

class SocialApiException extends RuntimeException {
    private static final String msg = "입력 정보를 다시 확인해 주세요.";

    public SocialApiException() {
    }

    public SocialApiException(String msg) {
        super(msg);
    }
}
