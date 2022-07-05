package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.MapResponseDto;
import com.sparta.meeting_platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MapService {
    private final PostRepository postRepository;

    String geocodingUrl="http://dapi.kakao.com/v2/local/search/address.json?query=";

    @Value("${geocoding}")
    private String geocoding;

    
    public void readMap(Double latitude, Double longitude, User user) {

//        double lati = 35.37158186664697;
//        double longi = 129.143196249161;
//
//        double theta = longitude - longi;
//        double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(lati))
//                + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(lati)) * Math.cos(deg2rad(theta));
//
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515 * 1.609344;
//
//        System.out.println("최종 거리" + dist + "km");

        List<MapResponseDto> mapResponseDtos = new ArrayList<>();

        List<Post> posts = postRepository.findAll();
        for(Post post : posts){
            double theta = longitude - post.getLongitude();
            double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(post.getLatitude()))
                    + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(post.getLatitude())) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1.609344;

            if(dist <= 50){
                //참여인원수
                //평균매너온도
                //대표 사진
//                mapResponseDtos.add(new MapResponseDto<>(post,));
            }
        }

    }


    public void searchMap(String location){



        URL obj;

        try{
            //인코딩한 String을 넘겨야 원하는 데이터를 받을 수 있다.
            String address = URLEncoder.encode(location, "UTF-8");

            obj = new URL(geocodingUrl+address);

            HttpURLConnection con = (HttpURLConnection)obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization",geocoding);
            con.setRequestProperty("content-type", "application/json");
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            Charset charset = Charset.forName("UTF-8");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            System.out.println(response);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
            System.out.println("-----------------------------------------------");
            JSONArray documents = (JSONArray) jsonObject.get("documents");
            System.out.println(documents);
            JSONObject thisAddress = (JSONObject) documents.get(0);
            String longitude = (String) thisAddress.get("x");
            String latitude = (String) thisAddress.get("y");
            double longi = Double.parseDouble(longitude);
            double lati = Double.parseDouble(latitude);

            System.out.println(longi);
            System.out.println(lati);
//            System.out.println(response.toString());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void detailsMap(List<String> categories, int joinCount, int distance,
                           Double latitude, Double longitude, User user) {

        List<Post> posts = postRepository.findAll();
        for(Post post : posts){
            double theta = longitude - post.getLongitude();
            double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(post.getLatitude()))
                    + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(post.getLatitude())) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1.609344;

            if(dist <= distance && (post.getPersonnel() == joinCount) && post.getCategories() == categories){
                //참여인원수
                //평균매너온도
                //대표 사진

//                mapResponseDtos.add();
            }
        }

    }

    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }

}
