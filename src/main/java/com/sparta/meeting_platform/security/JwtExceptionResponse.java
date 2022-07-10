package com.sparta.meeting_platform.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JwtExceptionResponse {
    private final Boolean response;
    private final String message;
//    private final HttpStatus httpStatus;


    public String convertToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); //ObjectMapper는 json 파싱할때 쓰는 객체
        return mapper.writeValueAsString(this); //writeValueAsString 문자열로 직렬화하는 메소드
    }
}