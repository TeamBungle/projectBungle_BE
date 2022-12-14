package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.chat.dto.UserDto;
import com.sparta.meeting_platform.chat.model.*;
import com.sparta.meeting_platform.chat.repository.*;
import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.MapDto.SearchMapDto;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.UserDto.MyPageDto;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.util.FileExtFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//
@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final S3Service s3Service;
    private final EntityManager em;
    private final PostSearchService postSearchService;
    private final MapSearchService mapSearchService;
    private final ChatRoomRepository chatRoomRepository;
    private final InvitedUsersRepository invitedUsersRepository;
    private final FileExtFilter fileExtFilter;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final ResignChatMessageJpaRepository resignChatMessageJpaRepository;
    private final ResignChatRoomJpaRepository resignChatRoomJpaRepository;

    private Double distance = 400000.0;

    public String formatDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    //????????? ?????? ??????(4??????)
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPosts(Long userId, Double latitude, Double longitude) throws ParseException {
        User user = checkUser(userId);
        Query realTimeQuery = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                        + "modified_at, personnel, place, time, title, user_id , "
                        + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                        + "FROM post AS p "
                        + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                        + "AND p.time < :convertedDateReal "
                        + "ORDER BY p.time desc", Post.class)
                .setParameter("convertedDateReal", formatDateTime())
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance)
                .setMaxResults(4);
        List<Post> realTimePosts = realTimeQuery.getResultList();
        Query endTimeQuery = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                        + "modified_at, personnel, place, time, title, user_id , "
                        + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                        + "FROM post AS p "
                        + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                        + "AND p.time > :convertedDateEnd "
                        + "ORDER BY p.time", Post.class)
                .setParameter("convertedDateEnd", formatDateTime())
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance)
                .setMaxResults(4);
        List<Post> endTimePosts = endTimeQuery.getResultList();
        Query mannerQuery = em.createNativeQuery("SELECT * FROM post AS p "
                        + "INNER JOIN (SELECT AVG(u.manner_temp) AS avg_temp, i.post_id AS id FROM invited_users AS i "
                        + "INNER JOIN userinfo AS u "
                        + "ON i.user_id = u.id "
                        + "GROUP BY i.post_id) AS s "
                        + "ON p.id = s.id "
                        + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                        + "AND p.time < :convertedDateReal "
                        + "ORDER BY avg_temp DESC", Post.class)
                .setParameter("convertedDateReal", formatDateTime())
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance)
                .setMaxResults(4);
        List<Post> mannerPosts = mannerQuery.getResultList();
        List<PostResponseDto> postListRealTime = postSearchService.searchTimeOrMannerPostList(realTimePosts, userId);
        List<PostResponseDto> postListEndTime = postSearchService.searchTimeOrMannerPostList(endTimePosts, userId);
        List<PostResponseDto> postListManner = postSearchService.searchTimeOrMannerPostList(mannerPosts, userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", user.getIsOwner(), postListRealTime, postListEndTime, postListManner), HttpStatus.OK);
    }

    //??????????????? ????????? ??????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsByCategories(Long userId, List<String> categories, Double latitude, Double longitude) throws ParseException {
        User user = checkUser(userId);
        String mergeList = postSearchService.categoryOrTagListMergeString(categories);
        Query query = em.createNativeQuery(
                        "SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance"
                                + " AND p.id in (select u.post_id from post_categories u"
                                + " WHERE u.category in (" + mergeList + "))"
                                + "ORDER BY distance", Post.class)
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance);
        List<Post> posts = query.getResultList();
        if (posts.size() < 1) {
            throw new PostApiException("???????????? ????????????, ?????? ??????????????? ??????????????????");
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postList, user.getIsOwner()), HttpStatus.OK);
    }

    //????????? ????????? ??????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsByTags(Long userId, List<String> tags, Double latitude, Double longitude) throws ParseException {
        User user = checkUser(userId);
        String mergeList = postSearchService.categoryOrTagListMergeString(tags);
        Query query = em.createNativeQuery(
                        "SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND p.id in (select u.post_id from post_tags u"
                                + " WHERE u.tag in (" + mergeList + ")) "
                                + "ORDER BY distance", Post.class)
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance);
        List<Post> posts = query.getResultList();

        if (posts.size() < 1) {
            throw new PostApiException("???????????? ????????????, ?????? ????????? ??????????????????");
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postList, user.getIsOwner()), HttpStatus.OK);
    }

    //????????? ??? ?????? ??????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> morePostList(Long userId, String status, Double latitude, Double longitude) throws ParseException {
        User user = checkUser(userId);
        List<Post> posts = new ArrayList<>();
        switch (status) {
            case "endTime":
                Query query = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND p.time > :convertedDate1"
                                + " ORDER BY p.time ", Post.class)
                        .setParameter("convertedDate1", formatDateTime())
                        .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                        .setParameter("distance", distance);
                posts = query.getResultList();
                break;
            case "realTime":
                Query query1 = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND p.time < :convertedDate1 "
                                + "ORDER BY p.time desc", Post.class)
                        .setParameter("convertedDate1", formatDateTime())
                        .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                        .setParameter("distance", distance);
                posts = query1.getResultList();
                break;
            case "manner":
                Query query2 = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "INNER JOIN (SELECT AVG(u.manner_temp) AS avg_temp, i.post_id AS id FROM invited_users AS i "
                                + "INNER JOIN userinfo AS u "
                                + "ON i.user_id = u.id "
                                + "GROUP BY i.post_id) AS s "
                                + "ON p.id = s.id "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND p.time > :convertedDate1 "
                                + "GROUP BY id "
                                + "ORDER BY avg_temp DESC", Post.class)
                        .setParameter("convertedDate1", formatDateTime())
                        .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                        .setParameter("distance", distance);
                posts = query2.getResultList();
                break;
        }
        if (posts.size() < 1) {
            throw new PostApiException("???????????? ????????????");
        }
        List<PostResponseDto> postList = postSearchService.searchTimeOrMannerPostList(posts, userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postList, user.getIsOwner()), HttpStatus.OK);
    }

    //????????? ?????? ??????
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> getPostsDetails(Long postId, Long userId) {
        User user = checkUser(userId);
        Post post = checkPost(postId);
        Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
        PostDetailsResponseDto postDetailsResponseDto = postSearchService.detailPost(like, post);
        Optional<List<InvitedUsers>> invitedUsers = Optional.ofNullable((invitedUsersRepository.findAllByUserId(userId)));

        if (!invitedUsers.isPresent()) {
            throw new PostApiException("????????? ?????? ??????");
        }
        for (InvitedUsers users : invitedUsers.get()) {
            if (users.getPostId().equals(postId)) {
                if (users.getReadCheck()) {
                    users.setReadCheck(false);
                    users.setReadCheckTime(LocalDateTime.now());
                }
            }
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postDetailsResponseDto, user.getIsOwner()), HttpStatus.OK);
    }

    // ????????? ??????
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> createPost(Long userId, PostRequestDto requestDto, List<MultipartFile> files) throws Exception {
        User user = checkUser(userId);
        Boolean isOwner = user.getIsOwner();

        if (requestDto.getContent().replaceAll("(\r\n|\r|\n|\n\r)", "").length() > 500) {
            throw new PostApiException("????????? ????????? 500??? ??????");
        }

        // isOwner ??? ??????
        if (isOwner) {
            throw new PostApiException("????????? ?????? ??????");
        } else {
            user.setIsOwner(true);
        }

        //???????????? ????????????
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime PromiseDateTime = LocalDateTime.parse(requestDto.getTime(), inputFormat);
        LocalDateTime now = LocalDateTime.now();
        if (!PromiseDateTime.isAfter(now.minusMinutes(10)) || PromiseDateTime.isAfter(now.plusDays(1))) {
            throw new PostApiException("??????????????? ???????????? ?????? ?????? 24?????? ????????? ???????????????.");
        }

        //????????????,??????,????????? ????????????
        List<String> categoryList
                = new ArrayList<>(Arrays.asList("??????", "??????", "?????????", "??????", "??????", "??????", "??????", "??????", "?????????", "??????"));

        for (String categroy : requestDto.getCategories()) {
            if (!categoryList.contains(categroy)) {
                throw new PostApiException("????????? ???????????? ?????????.");
            }
        }
        if (requestDto.getTags().size() > 3) {
            throw new PostApiException("?????? ?????? ????????? 3??? ?????????.");
        }
        for (String tag : requestDto.getTags()) {
            if (tag.length() > 10) {
                throw new PostApiException("10??? ????????? ????????? ??????????????????");
            }
        }
        if (requestDto.getPersonnel() > 50 || requestDto.getPersonnel() < 2) {
            throw new PostApiException("??????????????? 50??? ?????? ?????????");
        }

        //????????? s3?????? ??? ????????????
        if (files == null) {
            requestDto.setPostUrls(null);
        } else {
            if (files.size() > 3) {
                throw new PostApiException("????????? ????????? 3??? ?????? ?????????.");
            }
            List<String> postUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!fileExtFilter.badFileExt(file)) {
                    throw new PostApiException("???????????? ????????????.");
                }
                postUrls.add(s3Service.upload(file));
            }
            requestDto.setPostUrls(postUrls);
        }

        SearchMapDto searchMapDto = mapSearchService.findLatAndLong(requestDto.getPlace());
        Point point = mapSearchService.makePoint(searchMapDto.getLongitude(), searchMapDto.getLatitude());
        Post post = new Post(user, requestDto, searchMapDto.getLongitude(), searchMapDto.getLatitude(), point);
        postRepository.save(post);
        UserDto userDto = new UserDto(user);
        chatRoomRepository.createChatRoom(post, userDto);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", post.getId(), userId), HttpStatus.OK);
    }

    // ????????? ??????
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> updatePost(Long postId, Long userId, PostRequestDto requestDto, List<MultipartFile> files) throws Exception {

        checkUser(userId);
        Post post = checkPost(postId);

        List<String> postUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                postUrls.add(s3Service.upload(file));
            }
        }
        postUrls.addAll(requestDto.getPostUrls());
        requestDto.setPostUrls(postUrls);

        SearchMapDto searchMapDto = mapSearchService.findLatAndLong(requestDto.getPlace());
        Point point = mapSearchService.makePoint(searchMapDto.getLongitude(), searchMapDto.getLatitude());
        post.update(searchMapDto.getLongitude(), searchMapDto.getLatitude(), requestDto, point);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????"), HttpStatus.OK);
    }

    // ???????????? ?????? ????????? ???????????? ??????
    public ResponseEntity<FinalResponseDto<?>> getMyPost(Long userId) {
        User user = checkUser(userId);
        Post post = postRepository.findByUserId(userId);

        if (post.getPostUrls().size() < 1) {
            post.getPostUrls().add(null);
        }
        if (!user.getIsOwner()) {
            throw new PostApiException("???????????? ?????? ????????? ?????????");
        }
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postUrls(post.getPostUrls())
                .categories(post.getCategories())
                .tags(post.getTags())
                .time(post.getTime())
                .place(post.getPlace())
                .personnel(post.getPersonnel())
                .isLetter(post.getIsLetter())
                .build();

        return new ResponseEntity<>(
                new FinalResponseDto<>(
                        true, "????????? ?????? ????????? ?????? ??????",
                        postResponseDto, user.getIsOwner()), HttpStatus.OK);
    }

    //????????? ??????
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> deletePost(Long postId, Long userId) {
        Post post = checkPost(postId);
        User user = checkUser(userId);
        if (!post.getUser().getId().equals(userId)) {
            throw new PostApiException("?????? ???????????? ????????????.");
        } else {
            if (invitedUsersRepository.existsByPostId(postId)) {
                invitedUsersRepository.deleteAllByPostId(postId);
            }
            likeRepository.deleteByPostId(postId);
            postRepository.deleteById(postId);
            user.setIsOwner(false);
            ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(String.valueOf(postId));
            List<ChatMessage> chatMessage = chatMessageJpaRepository.findAllByRoomId(String.valueOf(postId));
            ResignChatRoom resignChatRoom = new ResignChatRoom(chatRoom);
            resignChatRoomJpaRepository.save(resignChatRoom);
            for (ChatMessage message : chatMessage) {
                ResignChatMessage resignChatMessage = new ResignChatMessage(message);
                resignChatMessageJpaRepository.save(resignChatMessage);
            }
            chatMessageJpaRepository.deleteByRoomId(String.valueOf(post.getId()));
            chatRoomJpaRepository.deleteByRoomId(String.valueOf(postId));
            return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", user.getIsOwner()), HttpStatus.OK);
        }
    }

    // ?????? ????????? ?????? ??????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getLikedPosts(Long userId,Double latitude, Double longitude) throws ParseException {
        User user = checkUser(userId);

        Query query = em.createNativeQuery(
                        "SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE p.id in (SELECT l.post_id FROM liketable AS l WHERE user_id = " + userId + " "
                                + "AND is_like = true )"
                                + "ORDER BY id", Post.class)
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude));
        List<Post> posts = query.getResultList();


        List<PostResponseDto> postList = postSearchService.searchLikePostList(posts, userId);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "???????????? ????????? ?????? ??????", postList, user.getIsOwner()), HttpStatus.OK);
    }

    // ?????? ?????? ????????? ??????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getMyPage(UserDetailsImpl userDetails) {
        User user = checkUser(userDetails.getUser().getId());
        MyPageDto myPageDto = new MyPageDto(user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "?????? ?????? ????????? ?????? ??????", myPageDto, user.getIsOwner()), HttpStatus.OK);
    }

    //??? ?????? ????????????
    public ResponseEntity<FinalResponseDto<?>> getMyPagePost(UserDetailsImpl userDetails) {
        User user = checkUser(userDetails.getUser().getId());
        Post post = postRepository.findByUserId(user.getId());
        Like like = likeRepository.findByUser_IdAndPost_Id(user.getId(), post.getId()).orElse(null);
        PostResponseDto postResponseDto = postSearchService.searchMyPost(like, post);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "?????? ?????? ????????? ?????? ??????", postResponseDto, user.getIsOwner()), HttpStatus.OK);
    }

    // ????????? ?????? ?????????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> morePostListInfiniteScroll(Long lastPoint, Long userId, String status, Double latitude, Double longitude, int size) throws ParseException {
        User user = checkUser(userId);
        List<Post> posts = new ArrayList<>();
        switch (status) {
            case "endTime":
                Query query = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND p.time > (SELECT post.time FROM post WHERE id = :lastId) "
                                + "ORDER BY p.time "
                                + "LIMIT :pageSize", Post.class)
                        .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                        .setParameter("distance", distance)
                        .setParameter("lastId", lastPoint)
                        .setParameter("pageSize", size);
                posts = query.getResultList();
                break;
            case "realTime":
                Query query1 = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND p.time < (SELECT post.time FROM post WHERE id = :lastId) "
                                + "ORDER BY p.time desc "
                                + "LIMIT :pageSize", Post.class)
                        .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                        .setParameter("distance", distance)
                        .setParameter("lastId", lastPoint)
                        .setParameter("pageSize", size);
                posts = query1.getResultList();
                break;
            case "manner":
                Query query2 = em.createNativeQuery("SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "INNER JOIN (SELECT AVG(u.manner_temp) AS avg_temp, i.post_id AS id FROM invited_users AS i "
                                + "INNER JOIN userinfo AS u "
                                + "ON i.user_id = u.id "
                                + "GROUP BY i.post_id) AS s "
                                + "ON p.id = s.id "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND p.time > :convertedDate1 "
                                + "AND avg_temp < :lastPoint "
                                + "GROUP BY id "
                                + "ORDER BY avg_temp DESC "
                                + "LIMIT :pageSize", Post.class)
                        .setParameter("lastPoint", lastPoint)
                        .setParameter("convertedDate1", formatDateTime())
                        .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                        .setParameter("distance", distance)
                        .setParameter("pageSize", size);
                posts = query2.getResultList();
                break;
        }

        if (posts.size() < 1) {
            throw new PostApiException("???????????? ????????????");
        }
        List<PostResponseDto> postList = postSearchService.searchTimeOrMannerPostList(posts, userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postList, user.getIsOwner()), HttpStatus.OK);
    }

    // ???????????? ?????? ?????? ?????????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getCategoriesInfiniteScroll(Long lastId, List<String> categories, Double latitude, Double longitude, Long userId, int size) throws ParseException {
        User user = checkUser(userId);
        String mergeList = postSearchService.categoryOrTagListMergeString(categories);
        Query query = em.createNativeQuery(
                        "SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(longitude, latitude)) > "
                                + "(SELECT ST_DISTANCE_SPHERE(:myPoint, POINT(longitude, latitude)) FROM post "
                                + "WHERE id = :lastId) "
                                + "AND ST_DISTANCE_SPHERE(:myPoint, POINT(longitude, latitude)) < :distance "
                                + "AND p.id in (select u.post_id from post_categories u "
                                + "WHERE u.category in (" + mergeList + ")) "
                                + "ORDER BY distance "
                                + "LIMIT :pageSize", Post.class)
                .setParameter("lastId", lastId)
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance)
                .setParameter("pageSize", size);

        List<Post> posts = query.getResultList();
        if (posts.size() < 1) {
            throw new PostApiException("??? ?????? ???????????? ???????????? ????????????.");
        }

        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postList, user.getIsOwner()), HttpStatus.OK);
    }

    // ?????? ?????? ?????? ?????????
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> gettagsInfiniteScroll(Long lastId, List<String> tags, Double latitude, Double longitude, Long userId, int size) throws ParseException {
        User user = checkUser(userId);
        String mergeList = postSearchService.categoryOrTagListMergeString(tags);
        Query query = em.createNativeQuery(
                        "SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(longitude, latitude)) > "
                                + "(SELECT ST_DISTANCE_SPHERE(:myPoint, POINT(longitude, latitude)) FROM post "
                                + "WHERE id = :lastId) "
                                + "AND ST_DISTANCE_SPHERE(:myPoint, POINT(longitude, latitude)) < :distance "
                                + "AND p.id in (select u.post_id from post_tags u "
                                + "WHERE u.tag in (" + mergeList + ")) "
                                + "ORDER BY distance "
                                + "LIMIT :pageSize", Post.class)
                .setParameter("lastId", lastId)
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance)
                .setParameter("pageSize", size);

        List<Post> posts = query.getResultList();
        if (posts.size() < 1) {
            throw new PostApiException("??? ?????? ???????????? ???????????? ????????????.");
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postList, user.getIsOwner()), HttpStatus.OK);
    }

    // ?????? ?????? ??????
    public User checkUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("?????? ????????? ?????? ??? ????????????."));
        return user;
    }

    // ????????? ?????? ??????
    public Post checkPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("???????????? ?????? ????????? ?????????."));
        return post;
    }

//    //????????? ?????? (????????? ????????? ?????????)
//    public ResponseEntity<FinalResponseDto<?>> getSearch(String keyword, Long userId, Double longitude, Double latitude) throws ParseException {
//        Optional<User> user = userRepository.findById(userId);
//
//        if (!user.isPresent()) {
//            return new ResponseEntity<>(new FinalResponseDto<>(false, "????????? ?????? ??????"), HttpStatus.BAD_REQUEST);
//        }
//        LocalDateTime localDateTime = LocalDateTime.now();
//        String convertedDate1 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        Query query = em.createNativeQuery("SELECT * FROM post AS p "
//                        + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance"
//                        + " AND p.time > :convertedDate1 AND p.id in (select u.post_id from post_categories u"
//                        + " WHERE u.category in ('" + keyword + "'))"
//                        + "ORDER BY p.time", Post.class)
//                .setParameter("convertedDate1", convertedDate1)
//                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
//                .setParameter("distance", 400000.0);
//        List<Post> posts = query.getResultList();
//
//        if (posts.size() < 1) {
//            return new ResponseEntity<>(new FinalResponseDto<>(false, "???????????? ????????????, ??????????????? ??????????????????"), HttpStatus.BAD_REQUEST);
//        }
//        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);
//        return new ResponseEntity<>(new FinalResponseDto<>(true, "????????? ?????? ??????", postList, user.get().getIsOwner()), HttpStatus.OK);
//    }
}

