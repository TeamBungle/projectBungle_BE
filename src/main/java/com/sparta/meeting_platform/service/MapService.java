package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.MapListDto;
import com.sparta.meeting_platform.dto.MapResponseDto;
import com.sparta.meeting_platform.dto.SearchMapDto;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.util.DeduplicationUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MapService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PostService postService;

    String geocodingUrl = "http://dapi.kakao.com/v2/local/search/address.json?query=";

    @Value("${geocoding}")
    private String geocoding;

    public ResponseEntity<MapResponseDto<?>> readMap(Double latitude, Double longitude, User user) throws java.text.ParseException {

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

        List<MapListDto> mapListDtos = new ArrayList<>();

        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            double theta = longitude - post.getLongitude();
            double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(post.getLatitude()))
                    + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(post.getLatitude())) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1.609344;

            if (dist <= 50) {

                Like like = likeRepository.findByUser_IdAndPost_Id(user.getId(), post.getId()).orElse(null);

                Boolean isLike;

                if(like == null){
                    isLike = false;
                }else {
                    isLike = like.getIsLike();
                }

                MapListDto mapListDto =MapListDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .personnel(post.getPersonnel())
                        .joinCount(1)                       //TODO 수정필요
                        .place(post.getPlace())
                        .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
                        .time(postService.timeCheck(post.getTime()))
                        .avgTemp(50)                      //TODO 수정필요
                        .isLetter(post.getIsLetter())
                        .isLike(isLike)
                        .latitude(post.getLatitude())
                        .longitude(post.getLongitude())
                        .build();

                mapListDtos.add(mapListDto);
            }
        }
        return new ResponseEntity<>(new MapResponseDto<>(true, "회원가입 성공",mapListDtos), HttpStatus.OK);

    }

    public ResponseEntity<MapResponseDto<?>> searchMap(String address,User user) throws IOException, ParseException, java.text.ParseException {
        SearchMapDto searchMapDto = searchLatAndLong(address);
        Double latitude = searchMapDto.getLatitude();
        Double longitude = searchMapDto.getLongitude();

        List<MapListDto> mapListDtos = new ArrayList<>();

        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            double theta = longitude - post.getLongitude();
            double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(post.getLatitude()))
                    + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(post.getLatitude())) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1.609344;

            if (dist <= 50) {

                Like like = likeRepository.findByUser_IdAndPost_Id(user.getId(), post.getId()).orElse(null);

                Boolean isLike;

                if(like == null){
                    isLike = false;
                }else {
                    isLike = like.getIsLike();
                }

                MapListDto mapListDto =MapListDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .personnel(post.getPersonnel())
                        .joinCount(1)                       //TODO 수정필요
                        .place(post.getPlace())
                        .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
                        .time(postService.timeCheck(post.getTime()))
                        .avgTemp(50)                      //TODO 수정필요
                        .isLetter(post.getIsLetter())
                        .isLike(isLike)
                        .latitude(post.getLatitude())
                        .longitude(post.getLongitude())
                        .build();

                mapListDtos.add(mapListDto);
            }
        }
        return new ResponseEntity<>(new MapResponseDto<>(true, "회원가입 성공",mapListDtos), HttpStatus.OK);
    }




    public ResponseEntity<MapResponseDto<?>> detailsMap(List<String> categories, int joinCount, int distance,
                           Double latitude, Double longitude, User user) throws java.text.ParseException {
        List<MapListDto> mapListDtos = new ArrayList<>();
//        List<Post> posts = postRepository.findAll();
        for (String category : categories) {
            List<Post> postList = postRepository.findAllByCategories(category);
            List<Post> posts = DeduplicationUtils.deduplication(postList, Post::getId);

            for (Post post : posts) {
                double theta = longitude - post.getLongitude();
                double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(post.getLatitude()))
                        + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(post.getLatitude())) * Math.cos(deg2rad(theta));

                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515 * 1.609344;

                if (dist <= distance && post.getPersonnel() == joinCount) {

                    Like like = likeRepository.findByUser_IdAndPost_Id(user.getId(), post.getId()).orElse(null);

                    Boolean isLike;

                    if (like == null) {
                        isLike = false;
                    } else {
                        isLike = like.getIsLike();
                    }

                    MapListDto mapListDto = MapListDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .personnel(post.getPersonnel())
                            .joinCount(1)                       //TODO 수정필요
                            .place(post.getPlace())
                            .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
                            .time(postService.timeCheck(post.getTime()))
                            .avgTemp(50)                      //TODO 수정필요
                            .isLetter(post.getIsLetter())
                            .isLike(isLike)
                            .latitude(post.getLatitude())
                            .longitude(post.getLongitude())
                            .build();

                    mapListDtos.add(mapListDto);
                }
            }
        }
        return new ResponseEntity<>(new MapResponseDto<>(true, "회원가입 성공",mapListDtos), HttpStatus.OK);
    }



    public SearchMapDto searchLatAndLong(String location) throws IOException, ParseException {

        URL obj;
        //인코딩한 String을 넘겨야 원하는 데이터를 받을 수 있다.
        String address = URLEncoder.encode(location, "UTF-8");

        obj = new URL(geocodingUrl + address);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization","KakaoAK " + geocoding);
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

        return new SearchMapDto(longi, lati);
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
