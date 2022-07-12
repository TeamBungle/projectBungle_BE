package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.dto.MapListDto;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.TempAndJoinCountSearchDto;
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
        String roomId = String.valueOf(postId);
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByRoomId(roomId);
        int temp = 0;
        int joinCount = 0;
        for (InvitedUsers invitedUser : invitedUsers) {
            temp += invitedUser.getUser().getMannerTemp();
           joinCount += 1;
        }
        int avgTemp = temp/joinCount;
        return new TempAndJoinCountSearchDto(joinCount,avgTemp);
    }
    public TempAndJoinCountSearchDto getJoinPeopleInfo(Long postId){
        String roomId = String.valueOf(postId);
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByRoomId(roomId);
        List<String> joinPeopleUrl = new ArrayList<>();
        List<String>  joinPeopleNickName =  new ArrayList<>();
        int temp = 0;
        int joinCount = 0;
        for (InvitedUsers invitedUser : invitedUsers) {
            temp += invitedUser.getUser().getMannerTemp();
            joinCount += 1;
           joinPeopleUrl.add(invitedUser.getUser().getProfileUrl());
           joinPeopleNickName.add(invitedUser.getUser().getNickName());
        }
        int avgTemp = temp/joinCount;
        return new TempAndJoinCountSearchDto(joinPeopleUrl,joinPeopleNickName,avgTemp,joinCount);
    }

    //카페고리및태그 리스트->스트링 변환
    public String categoryOrTagListMergeString (List<String> categoryOrTagList){
        String mergeList = "";
        for (String string : categoryOrTagList) {
            mergeList += "'" + string + "',";
        }
        mergeList = mergeList.substring(0, mergeList.length() - 1);
        return mergeList;
    }

    //postlist 찾기
    public List<PostResponseDto> searchPostList(List<Post> posts, Long userId){
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
                    .build();
            postList.add(postResponseDto);
        }
        return postList;
    }

    //지도에서 post리스트 찾기
    public List<MapListDto> searchMapPostList(List<Post> posts, Long userId) {
        List<MapListDto> mapListDtos = new ArrayList<>();
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
                .joinPeopleUrl(tempAndJoinCountSearchDto.getJoinPeopleUrl())
                .joinPeopleNickname(tempAndJoinCountSearchDto.getJoinPeopleNickName())
                .joinCount(tempAndJoinCountSearchDto.getJoinCount())
                .isLetter(post.getIsLetter())
                .isLike(isLike)
                .build();
        return postDetailsResponseDto;
    }
    //찜한 post List 찾기
    public List<PostResponseDto> searchLikePostList(List<PostMapping> posts, Long userId) {
        List<PostResponseDto> postList = new ArrayList<>();
        for (PostMapping post : posts) {
            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getPost().getId()).orElseThrow(
                    () -> new NullPointerException("찜한 게시글이 없습니다.")
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
            return duration.getSeconds() / 60 + "분 경과";
        }
        return localDateTime.getHour() + "시 시작 예정";
    }
}
