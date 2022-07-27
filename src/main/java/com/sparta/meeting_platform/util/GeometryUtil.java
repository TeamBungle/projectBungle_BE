package com.sparta.meeting_platform.util;


public class GeometryUtil {

    public static Location calculate(Double baseLatitude, Double baseLongitude, Double distance,
                                     Double bearing) {
        Double radianLatitude = toRadian(baseLatitude);
        Double radianLongitude = toRadian(baseLongitude);
        Double radianAngle = toRadian(bearing);
        Double distanceRadius = distance / 6371.01;

        Double latitude = Math.asin(sin(radianLatitude) * cos(distanceRadius) +
                cos(radianLatitude) * sin(distanceRadius) * cos(radianAngle));
        Double longitude = radianLongitude + Math.atan2(sin(radianAngle) * sin(distanceRadius) *
                cos(radianLatitude), cos(distanceRadius) - sin(radianLatitude) * sin(latitude));

        longitude = normalizeLongitude(longitude);
        return new Location(toDegree(latitude), toDegree(longitude));
    }

    /*
     * coordinate : 각도 (위도 값, 경도 값)
     * Math.PI : 윈주율
     * Radian : 호의 길이가 반지름과 같을 때 1
     * degrees => radians : 각도 * 원주율 / 180
     * radians => degrees : 라디안 * 180 / 원주율
     */

    private static Double toRadian(Double coordinate) {
        return coordinate * Math.PI / 180.0;
    }

    private static Double toDegree(Double coordinate) {
        return coordinate * 180.0 / Math.PI;
    }

    private static Double sin(Double coordinate) {
        return Math.sin(coordinate);
    }

    private static Double cos(Double coordinate) {
        return Math.cos(coordinate);
    }

    private static Double normalizeLongitude(Double longitude) {
        return (longitude + 540) % 360 - 180;
    }

//            return "<!DOCTYPE html>\n" +
//                    "<html lang=\"en\">\n" +
//                    "<head>\n" +
//                    "    <meta charset=\"UTF-8\">\n" +
//                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
//                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
//                    "\n" +
//                    "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\"\n" +
//                    "          integrity=\"sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC\" crossorigin=\"anonymous\">\n" +
//                    "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" +
//                    "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js\"\n" +
//                    "            integrity=\"sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM\"\n" +
//                    "            crossorigin=\"anonymous\"></script>\n" +
//                    "\n" +
//                    "    <link href=\"https://fonts.googleapis.com/css2?family=Gowun+Dodum&display=swap\" rel=\"stylesheet\">\n" +
//                    "\n" +
//                    "    <title>벙글! 이메일 인증</title>\n" +
//                    "\n" +
//                    "    <style>\n" +
//                    "        * {\n" +
//                    "            font-family: 'Noto Sans KR', sans-serif;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .email-wrap{\n" +
//                    "            display: flex;\n" +
//                    "            flex-direction: column;\n" +
//                    "            width: 375px;\n" +
//                    "            height: 623px;\n" +
//                    "            margin: auto;\n" +
//                    "            padding:15px;\n" +
//                    "            border: 2px solid #FFC632;\n" +
//                    "        }\n" +
//                    "        .title-div{\n" +
//                    "            display: flex;\n" +
//                    "            flex-direction: column;\n" +
//                    "            align-items: center;\n" +
//                    "        }\n" +
//                    "        .title-logo{\n" +
//                    "            width: 205px;\n" +
//                    "            height: 47px;\n" +
//                    "            margin-top: 35px;\n" +
//                    "            margin-bottom: 7px;\n" +
//                    "        }\n" +
//                    "        .title-sub{\n" +
//                    "            font-family: 'Noto Sans KR';\n" +
//                    "            font-style: normal;\n" +
//                    "            font-weight: 400;\n" +
//                    "            font-size: 18px;\n" +
//                    "            line-height: 26px;\n" +
//                    "            color: #545454;\n" +
//                    "            margin-bottom: 18px;\n" +
//                    "        }\n" +
//                    "        .bungle-illustration{\n" +
//                    "            width: 290px;\n" +
//                    "            height: 161px;\n" +
//                    "            margin-bottom: 43px;\n" +
//                    "        }\n" +
//                    "        .email-content-wrap{\n" +
//                    "            width: 86%;\n" +
//                    "            margin: auto;\n" +
//                    "        }\n" +
//                    "\n" +
//                    "        .email-content-title{\n" +
//                    "            font-style: normal;\n" +
//                    "            font-weight: 400;\n" +
//                    "            font-size: 20px;\n" +
//                    "            line-height: 150%;\n" +
//                    "            /* identical to box height, or 30px */\n" +
//                    "\n" +
//                    "            color: #000000;\n" +
//                    "            margin-bottom: 6px;\n" +
//                    "        }\n" +
//                    "        .email-content{\n" +
//                    "            font-style: normal;\n" +
//                    "            font-weight: 400;\n" +
//                    "            font-size: 12px;\n" +
//                    "            line-height: 150%;\n" +
//                    "            /* or 18px */\n" +
//                    "            color: #000000;\n" +
//                    "            margin-bottom: 10px;\n" +
//                    "        }\n" +
//                    "        .email-content-sub{\n" +
//                    "            font-style: normal;\n" +
//                    "            font-weight: 400;\n" +
//                    "            font-size: 9px;\n" +
//                    "            line-height: 150%;\n" +
//                    "            /* identical to box height, or 14px */\n" +
//                    "            /* Gray */\n" +
//                    "            color: #898989;\n" +
//                    "            margin-bottom: 43px;\n" +
//                    "        }\n" +
//                    "        .email-confirm-complete{\n" +
//                    "            display: flex;\n" +
//                    "            width: 281px;\n" +
//                    "            height: 53px;\n" +
//                    "            /* maincolor */\n" +
//                    "            background-color: #FFC632;\n" +
//                    "            border-radius: 10px;\n" +
//                    "\n" +
//                    "            font-style: normal;\n" +
//                    "            font-weight: 400;\n" +
//                    "            font-size: 18px;\n" +
//                    "            line-height: 26px;\n" +
//                    "            justify-content: center;\n" +
//                    "            align-items: center;\n" +
//                    "            text-align: center;\n" +
//                    "            color: #000000;\n" +
//                    "        }\n" +
//                    "        .email-redirection{\n" +
//                    "            text-decoration-line: none;\n" +
//                    "            color: black;\n" +
//                    "        }\n" +
//                    "    </style>\n" +
//                    "    <script>\n" +
//                    "\n" +
//                    "    </script>\n" +
//                    "</head>\n" +
//                    "<body>\n" +
//                    "<div class=\"email-wrap\">\n" +
//                    "    <div class=\"title-div\">\n" +
//                    "        <img class=\"title-logo\" src=\"https://user-images.githubusercontent.com/87007109/181164515-7a5a4135-94c0-4e4b-b9e4-759bb39340d0.jpg\" alt=\"src에 s3 경로\"/>\n" +
//                    "        <div class=\"title-sub\">너와 나 친구되는 시간</div>\n" +
//                    "        <img class=\"bungle-illustration\" src=\"https://user-images.githubusercontent.com/87007109/181164595-fbb875e7-6780-46d4-8bc6-e34dcf50d898.jpg\" alt=\"src에 s3 경로를 넣어주시면 됩니다.\"/>\n" +
//                    "        <div class=\"email-content-wrap\">\n" +
//                    "            <div class=\"email-content-title\">안녕하세요</div>\n" +
//                    "            <div class=\"email-content\">벙글의 새로운 회원이 되신 것을 환영합니다.<br/>안전한 서비스를 위하여 인증 제한시간인 <span style=\"color: red\">15</span>분 이내에 <br/>이메일 인증을 완료하여 주시기 바랍니다.</div>\n" +
//                    "            <div class=\"email-content-sub\">본 메일은 발신 전용으로 회신되지 않습니다.</div>\n" +
//                    "            <div class=\"email-confirm-complete\"><a class=\"email-redirection\" href='https://fifaonline4-bk.shop/user/confirmEmail?token=" + emailToken  +"'>메일 인증 완료하기</a></div>\n" +
//            "        </div>\n" +
//            "    </div>\n" +
//            "</body>\n" +
//            "</html>";
//}
}
