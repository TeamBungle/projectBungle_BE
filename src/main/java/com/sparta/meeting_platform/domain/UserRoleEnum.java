package com.sparta.meeting_platform.domain;

public enum UserRoleEnum {

//    BEFORE_CONFIRM(false),  // 사용자 권한
//    OK_CONFIRM(true);  // 관리자 권한
//
//    boolean code;
//
//    UserRoleEnum(boolean code) {
//        this.code = code;
//    }
    NEW_USER(Authority.NEW_USER),
    USER(Authority.USER),
    RESIGN_USER(Authority.RESIGN_USER),
    STOP_USER(Authority.STOP_USER),
    ADMIN(Authority.ADMIN);

    private final String authority;

    UserRoleEnum(String authority){
        this.authority = authority;
    }

    public String getAuthority(){
        return this.authority;
    }

    public static class Authority {
        public static final String NEW_USER = "ROLE_NEW";
        public static final String USER = "ROLE_USER";
        public static final String RESIGN_USER = "ROLE_RESIGN";
        public static final String STOP_USER = "ROLE_STOP";
        public static final String ADMIN = "ROLE_ADMIN";
    }

}
