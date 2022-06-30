package com.sparta.meeting_platform.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiException {
    private Boolean response;
    private String Message;
}
