package com.sparta.meeting_platform.service;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SocialNaverService {

    // 클라이언트로 부터 받은 code, state 를 네이버로 전달하여 access_Token 받는 함수
    public String getNaverAccessToken(String code, String state) {
        String access_Token = "";
        String refresh_Token;
        String reqURL = "https://nid.naver.com/oauth2.0/token";

        try {
            JsonElement element = jsonElement(reqURL, null, code, state);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(access_Token);
        createNaverUser(access_Token);
        return access_Token;
    }

    // 받은 토큰을 사용하여 유저 정보 가져오는 함수
    public void createNaverUser(String token) {

        String reqURL = "https://openapi.naver.com/v1/nid/me";

        try {
            JsonElement element = jsonElement(reqURL, token, null, null);

            String profileImageValue = String.valueOf(element.getAsJsonObject().get("response").getAsJsonObject().get("profile_image"));
            String profileImage = profileImageValue.replaceAll("\\\\","" );
            System.out.println("id : " + element.getAsJsonObject().get("response").getAsJsonObject().get("id"));
            System.out.println("email : " + element.getAsJsonObject().get("response").getAsJsonObject().get("email"));
            System.out.println("profile_image : " + element.getAsJsonObject().get("response").getAsJsonObject().get("profile_image"));
            System.out.println(profileImage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonElement jsonElement(String reqURL, String token, String code, String state) throws IOException {
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        //전송할 header 작성, access_token전송
        if (token == null) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=authorization_code" +  // TODO grant_type 입력
                    "&client_id=fNINb0JLOoWHKPO8p2HO" + // TODO client-id 입력
                    "&client_secret=5PzjffB3yr" + // TODO client_secret 입력
                    "&redirect_uri=http://localhost:3000/oauth/callback/naver" + // TODO 인가코드 받은 redirect_uri 입력
                    "&code=" + code +
                    "&state=" + state;
            bw.write(sb);
            bw.flush();
            bw.close();
        } else
            conn.setRequestProperty("Authorization", "Bearer " + token);

        //결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode : " + responseCode);

        //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();

        System.out.println("response body : " + result);

        //Gson 라이브러리에 포함된 클래스로 JSON 파싱 객체 생성
        return JsonParser.parseString(result.toString());
    }
}
