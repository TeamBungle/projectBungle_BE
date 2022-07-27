package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.dto.MapListDto;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.TempAndJoinCountSearchDto;
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
    private final InvitedUsersRepository invitedUsersRepository;

    public TempAndJoinCountSearchDto getAvgTemp(Long postId){
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByPostId(postId);
        try{
            int temp = 0;
            int joinCount = 0;
            for (InvitedUsers invitedUser : invitedUsers) {
                temp += invitedUser.getUser().getMannerTemp();
                joinCount += 1;
            }
            int avgTemp = temp/joinCount;
            return new TempAndJoinCountSearchDto(joinCount,avgTemp);
        } catch (ArithmeticException e){
            return new TempAndJoinCountSearchDto(0,0);
        }

//            int temp = 0;
//            int joinCount = 0;
//            for (InvitedUsers invitedUser : invitedUsers) {
//                temp += invitedUser.getUser().getMannerTemp();
//                joinCount += 1;
//            }
//            int avgTemp = temp/joinCount;
//            return new TempAndJoinCountSearchDto(joinCount,avgTemp);


    }
    public TempAndJoinCountSearchDto getJoinPeopleInfo(Long postId){
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByPostId(postId);
        List<String> joinPeopleUrl = new ArrayList<>();
        List<String>  joinPeopleNickName =  new ArrayList<>();
        List<String> joinPeopleIntro = new ArrayList<>();
        try {
            int temp = 0;
            int joinCount = 0;
            for (InvitedUsers invitedUser : invitedUsers) {
                temp += invitedUser.getUser().getMannerTemp();
                joinCount += 1;
                joinPeopleUrl.add(invitedUser.getUser().getProfileUrl());
                joinPeopleNickName.add(invitedUser.getUser().getNickName());
                joinPeopleIntro.add(invitedUser.getUser().getIntro());

            }
            int avgTemp = temp/joinCount;
            return new TempAndJoinCountSearchDto(joinPeopleUrl,joinPeopleNickName,joinPeopleIntro,avgTemp,joinCount);
        } catch (ArithmeticException e){
            return new TempAndJoinCountSearchDto(joinPeopleUrl,joinPeopleNickName,joinPeopleIntro,0,0);
        }


    }

    //카페고리및태그 리스트->스트링 변환
    public String categoryOrTagListMergeString (List<String> categoryOrTagList){
        String mergeList = "";
        try{
            for (String string : categoryOrTagList) {
                mergeList += "'" + string + "',";
            }
            mergeList = mergeList.substring(0, mergeList.length() - 1);
            return mergeList;
        } catch (StringIndexOutOfBoundsException e){
            return mergeList = "'맛집','카페','노래방','운동','친목','전시','여행','쇼핑','스터디','게임'";
        }
    }


    //postlist 찾기 - 거리순
    public List<PostResponseDto> searchPostList(List<Post> posts, Long userId, Double longitude,Double latitude){
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
//                    .distance(dist)
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
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
            TempAndJoinCountSearchDto tempAndJoinCountSearchDto = getAvgTemp(post.getId());
            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .personnel(post.getPersonnel())
                    .joinCount(tempAndJoinCountSearchDto.getJoinCount())
                    .place(post.getPlace())
                    .postUrl(post.getPostUrls().get(0))
                    .time(timeCheck(post.getTime()))
                    .avgTemp(tempAndJoinCountSearchDto.getAveTemp())
                    .isLetter(post.getIsLetter())
                    .isLike(isLike)
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
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
            TempAndJoinCountSearchDto tempAndJoinCountSearchDto = getAvgTemp(post.getId());
            MapListDto mapListDto = MapListDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .personnel(post.getPersonnel())
                    .joinCount(tempAndJoinCountSearchDto.getJoinCount())
                    .place(post.getPlace())
                    .postUrl(post.getPostUrls().get(0))
                    .time(post.getTime())
                    .avgTemp(tempAndJoinCountSearchDto.getAveTemp())
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
        TempAndJoinCountSearchDto tempAndJoinCountSearchDto = getJoinPeopleInfo(post.getId());
        PostDetailsResponseDto postDetailsResponseDto = PostDetailsResponseDto.builder()
                .postId(post.getId())
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
                .joinPeopleIntro(tempAndJoinCountSearchDto.getJoinPeopleIntro())
                .joinPeopleUrl(tempAndJoinCountSearchDto.getJoinPeopleUrl())
                .joinPeopleNickname(tempAndJoinCountSearchDto.getJoinPeopleNickName())
                .joinCount(tempAndJoinCountSearchDto.getJoinCount())
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


            TempAndJoinCountSearchDto tempAndJoinCountSearchDto1 = getAvgTemp(post.getPost().getId());
            PostResponseDto postResponseDto = PostResponseDto.builder()
                    .id(post.getPost().getId())
                    .title(post.getPost().getTitle())
                    .content(post.getPost().getContent())
                    .personnel(post.getPost().getPersonnel())
                    .joinCount(tempAndJoinCountSearchDto1.getJoinCount())
                    .place(post.getPost().getPlace())
                    .postUrl(post.getPost().getPostUrls().get(0))
                    .time(timeCheck(post.getPost().getTime()))
                    .avgTemp(tempAndJoinCountSearchDto1.getAveTemp())
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

        TempAndJoinCountSearchDto tempAndJoinCountSearchDto1 = getAvgTemp(post.getId());
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .personnel(post.getPersonnel())
                .joinCount(tempAndJoinCountSearchDto1.getJoinCount())
                .place(post.getPlace())
                .postUrl(post.getPostUrls().get(0))
                .time(timeCheck(post.getTime()))
                .avgTemp(tempAndJoinCountSearchDto1.getAveTemp())
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
            if(duration.getSeconds() < 60 * 60){
                return duration.getSeconds() / 60 + "분 경과";
            } else {
                return duration.getSeconds() / 60 / 60 + "시 경과";
            }

        }else{
            if(localDateTime.getDayOfMonth()==LocalDateTime.now().getDayOfMonth()){
                return localDateTime.getHour() + "시 시작 예정";
            } else {
                return "내일 " + localDateTime.getHour() + "시 시작 예정";
            }

        }
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
