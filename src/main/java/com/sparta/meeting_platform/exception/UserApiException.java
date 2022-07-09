package com.sparta.meeting_platform.exception;

public class UserApiException extends RuntimeException {
    private static final String msg = "입력 정보를 다시 확인해 주세요.";
    public UserApiException(){}
    public UserApiException(String msg){super(msg);}
}

