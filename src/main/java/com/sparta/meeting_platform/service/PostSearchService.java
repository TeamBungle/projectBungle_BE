package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.dto.MapListDto;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.SearchMapDto;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.mapping.PostMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostSearchService {
    private final LikeRepository likeRepository;

    //카페고리및태그 리스트->스트링 변환
    public String categoryOrTagListMergeString (List<String> categoryOrTagList){
        String mergeList = "";
        for (String string : categoryOrTagList) {
            mergeList += "'" + string + "',";
        }
        mergeList = mergeList.substring(0, mergeList.length() - 1);
        return mergeList;
    }

    //postlist 찾기 - 거리순
    public List<PostResponseDto> searchPostList(List<Post> posts, Long userId, Double longitude,Double latitude){
        List<PostResponseDto> postList = new ArrayList<>();
        for (Post post : posts) {
            double theta = longitude - post.getLongitude();
            double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(post.getLatitude()))
                    + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(post.getLatitude())) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1.609344;
            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
            Boolean isLike;

            if (like == null) {
                isLike = false;
            } else {
                isLike = like.getIsLike();
            }
            if (post.getPostUrls().size() < 1) {
                post.getPostUrls().add(null);
            }
            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .personnel(post.getPersonnel())
                    .joinCount(1)                       //TODO 수정필요
                    .place(post.getPlace())
                    .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
                    .time(timeCheck(post.getTime()))
                    .avgTemp(50)                      //TODO 수정필요
                    .isLetter(post.getIsLetter())
                    .isLike(isLike)
                    .distance(dist)
                    .build();
            postList.add(postResponseDto);
        }
        return postList;
    }

    //postlist 찾기 - realTime,endTime,manner
    public List<PostResponseDto> searchTimeOrMannerPostList(List<Post> posts, Long userId){
        List<PostResponseDto> postList = new ArrayList<>();
        for (Post post : posts) {
            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
            Boolean isLike;

            if (like == null) {
                isLike = false;
            } else {
                isLike = like.getIsLike();
            }
            if (post.getPostUrls().size() < 1) {
                post.getPostUrls().add(null);
            }
            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .personnel(post.getPersonnel())
                    .joinCount(1)                       //TODO 수정필요
                    .place(post.getPlace())
                    .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
                    .time(timeCheck(post.getTime()))
                    .avgTemp(50)                      //TODO 수정필요
                    .isLetter(post.getIsLetter())
                    .isLike(isLike)
                    .build();
            postList.add(postResponseDto);
        }
        return postList;
    }

    //지도에서 post리스트 찾기
    public List<MapListDto> searchMapPostList(List<Post> posts, Long userId, Double longitude , Double latitude) {
        List<MapListDto> mapListDtos = new ArrayList<>();
        for (Post post : posts) {
            double theta = longitude - post.getLatitude();
            double dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(post.getLongitude()))
                    + Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(post.getLongitude())) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1.609344;

            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
            Boolean isLike;

            if (like == null) {
                isLike = false;
            } else {
                isLike = like.getIsLike();
            }
            if (post.getPostUrls().size() < 1) {
                post.getPostUrls().add(null);
            }
            MapListDto mapListDto = MapListDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .personnel(post.getPersonnel())
                    .joinCount(1)                       //TODO 수정필요
                    .place(post.getPlace())
                    .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
                    .time(post.getTime())
                    .avgTemp(50)                      //TODO 수정필요
                    .isLetter(post.getIsLetter())
                    .isLike(isLike)
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
                    .distance(dist)
                    .build();

            mapListDtos.add(mapListDto);
        }
        return mapListDtos;
    }
    public PostDetailsResponseDto detailPost(Like like, Post post) {
        Boolean isLike;

        if (like == null) {
            isLike = false;
        } else {
            isLike = like.getIsLike();
        }
        if (post.getPostUrls().size() < 1) {
            post.getPostUrls().add(null);
        }
        List<String> joinPeopleurls = new ArrayList<>(); //TODO 수정필요
        joinPeopleurls.add("test1");
        joinPeopleurls.add("test2");
        List<String> joinPeopleNicknames = new ArrayList<>(); //TODO 수정필요
        joinPeopleNicknames.add("test1");
        joinPeopleNicknames.add("test2");
        PostDetailsResponseDto postDetailsResponseDto = PostDetailsResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .time(timeCheck(post.getTime()))
                .personnel(post.getPersonnel())
                .place(post.getPlace())
                .postUrls(post.getPostUrls())
                .tags(post.getTags())
                .categories(post.getCategories())
                .bungCount(post.getUser().getBungCount())
                .mannerTemp(post.getUser().getMannerTemp())
                .joinPeopleUrl(joinPeopleurls)                //TODO 수정필요
                .joinPeopleNickname(joinPeopleNicknames)           //TODO 수정필요
                .joinCount(1)                       //TODO 수정필요
                .isLetter(post.getIsLetter())
                .isLike(isLike)
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .build();
        return postDetailsResponseDto;
    }
    //찜한 post List 찾기
    public List<PostResponseDto> searchLikePostList(List<PostMapping> posts, Long userId) {
        List<PostResponseDto> postList = new ArrayList<>();
        for (PostMapping post : posts) {
            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getPost().getId()).orElseThrow(
                    () -> new PostApiException("찜한 게시글이 없습니다.")
            );

            if (post.getPost().getPostUrls().size() < 1) {
                post.getPost().getPostUrls().add(null);
            }

            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .id(post.getPost().getId())
                    .title(post.getPost().getTitle())
                    .content(post.getPost().getContent())
                    .personnel(post.getPost().getPersonnel())
                    .joinCount(1)                                     //TODO 수정필요
                    .place(post.getPost().getPlace())
                    .postUrl(post.getPost().getPostUrls().get(0))    //TODO 수정필요
                    .time(timeCheck(post.getPost().getTime()))
                    .avgTemp(50)                                  //TODO 수정필요
                    .isLetter(post.getPost().getIsLetter())
                    .isLike(like.getIsLike())
                    .build();
            postList.add(postResponseDto);
        }
        return postList;
    }

    //나의 post 찾기
    public PostResponseDto searchMyPost(Like like, Post post) {
        Boolean isLike;

        if (like == null) {
            isLike = false;
        } else {
            isLike = like.getIsLike();
        }
        if (post.getPostUrls().size() < 1) {
            post.getPostUrls().add(null);
        }
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .personnel(post.getPersonnel())
                .joinCount(1)                       //TODO 수정필요
                .place(post.getPlace())
                .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
                .time(timeCheck(post.getTime()))
                .avgTemp(50)                      //TODO 수정필요
                .isLetter(post.getIsLetter())
                .isLike(isLike)
                .build();
        return postResponseDto;
    }



    // Time 변환
    public String timeCheck(String time) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(time, inputFormat);
        if (!localDateTime.isAfter(LocalDateTime.now())) {
            Duration duration = Duration.between(localDateTime, LocalDateTime.now());
            return duration.getSeconds() / 60 + "분 경과";
        }
        return localDateTime.getHour() + "시 시작 예정";
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
