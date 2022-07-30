package com.sparta.meeting_platform.util;

import org.springframework.stereotype.Component;

@Component
public class EmailForm {

    public String getEmailBody(String emailToken) {

        return " <div style='text-align:center;'> <img class=\"title-logo\" src=\"https://meeting-project.s3.ap-northeast-2.amazonaws.com/%EB%A9%94%EC%9D%BC%EC%9D%B8%EC%A6%9D+%EC%9D%B4%EB%AF%B8%EC%A7%80.png\" alt=\"src에 s3 경로\"/>\n" +
                " <div class=\"email-confirm-complete\"><a class=\"email-redirection\" href='https://fifaonline4-bk.shop/user/confirmEmail?token=" + emailToken +"'>" +
                "<img src=https://user-images.githubusercontent.com/107230384/181713305-b65e2988-bd2c-4a9e-b886-584296265a60.png></a></div></div>\n";
    }
}
