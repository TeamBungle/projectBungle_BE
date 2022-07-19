package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.chat.dto.UserDto;
import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.SearchMapDto;
import com.sparta.meeting_platform.dto.user.MyPageDto;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.repository.mapping.PostMapping;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.util.FileExtFilter;
import com.sparta.meeting_platform.util.PostListComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;


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
    private Double distance = 400.0;
    private final ChatRoomRepository chatRoomRepository;
    private final InvitedUsersRepository invitedUsersRepository;

    private final FileExtFilter fileExtFilter;


    //게시글 전체 조회(4개만)
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPosts(Long userId, Double latitude, Double longitude) {
        User user = checkUser(userId);
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        LocalDateTime localDateTime = LocalDateTime.now();
        String convertedDate1 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String convertedDate2 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Query realTimeQuery = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + "AND p.time < :convertedDate1"
                        + " ORDER BY p.time desc", Post.class)
                .setParameter("convertedDate1", convertedDate1)
                .setMaxResults(4);
        List<Post> realTimePosts = realTimeQuery.getResultList();
        Query endTimeQuery = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + "AND p.time > :convertedDate2"
                        + " ORDER BY p.time", Post.class)
                .setParameter("convertedDate2", convertedDate2)
                .setMaxResults(4);
        List<Post> endTimePosts = endTimeQuery.getResultList();

        List<PostResponseDto> postListRealTime = postSearchService.searchTimeOrMannerPostList(realTimePosts, userId);
        List<PostResponseDto> postListEndTime = postSearchService.searchTimeOrMannerPostList(endTimePosts, userId);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", user.getIsOwner(), postListRealTime, postListEndTime), HttpStatus.OK);
    }// manner도 같이 보내줘야함
    // realtime은 지난 시간만 , 아이디 순인지 시간순인지 확인
    // endtime은 지나지 않는 시간만


    //카테고리별 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsByCategories(Long userId, List<String> categories, Double latitude, Double longitude) {
        checkUser(userId);
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        String mergeList = postSearchService.categoryOrTagListMergeString(categories);
        LocalDateTime localDateTime = LocalDateTime.now();
        String convertedDate1 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Query query = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + " AND p.time > :convertedDate1 AND p.id in (select u.post_id from post_categories u"
                        + " WHERE u.category in (" + mergeList + "))"
                        + " ORDER BY p.time ", Post.class)
                .setParameter("convertedDate1", convertedDate1);
        List<Post> posts = query.getResultList();
        if (posts.size() < 1) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다, 다른 카테고리로 조회해주세요"), HttpStatus.OK);
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId, longitude, latitude);
        Collections.sort(postList, new PostListComparator());
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }

    //게시글 조회 (제목에 포함된 단어로)
    public ResponseEntity<FinalResponseDto<?>> getSearch(String keyword, Long userId, Double longitude, Double latitude) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 검색 실패"), HttpStatus.BAD_REQUEST);
        }
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        LocalDateTime localDateTime = LocalDateTime.now();
        String convertedDate1 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Query query = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + " AND p.time > :convertedDate1 AND p.id in (select u.post_id from post_categories u"
                        + " WHERE u.category in ('" + keyword + "'))"
                        + "ORDER BY p.time", Post.class)
                .setParameter("convertedDate1", convertedDate1);
        List<Post> posts = query.getResultList();

        if (posts.size() < 1) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다, 다른단어로 검색해주세요"), HttpStatus.BAD_REQUEST);
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId, longitude, latitude);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }


    //태그별 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsByTags(Long userId, List<String> tags, Double latitude, Double longitude) {
        checkUser(userId);
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        String mergeList = postSearchService.categoryOrTagListMergeString(tags);
        Query query = em.createNativeQuery("SELECT * FROM post AS p "
                + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                + " AND p.id in (select u.post_id from post_tags u"
                + " WHERE u.tag in (" + mergeList + "))", Post.class);
        List<Post> posts = query.getResultList();

        if (posts.size() < 1) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다, 다른 태그로 조회해주세요"), HttpStatus.OK);
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId, longitude, latitude);
        Collections.sort(postList, new PostListComparator());
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }


    //게시글 더 보기 조회 추가 해야함
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> morePostList(Long userId, String status, Double latitude, Double longitude) {
        checkUser(userId);
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        List<Post> posts = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        String convertedDate1 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        switch (status) {
            case "endTime":
                Query query = em.createNativeQuery("SELECT * FROM post AS p "
                                + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                                + "AND p.time > :convertedDate1"
                                + " ORDER BY p.time ", Post.class)
                        .setParameter("convertedDate1", convertedDate1);
                posts = query.getResultList();
                break;
            case "realTime":
                Query query1 = em.createNativeQuery("SELECT * FROM post AS p "
                                + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                                + "AND p.time < :convertedDate1"
                                + " ORDER BY p.time desc", Post.class)
                        .setParameter("convertedDate1", convertedDate1);
                posts = query1.getResultList();
                break;
            case "manner":
                Query query2 = em.createNativeQuery("SELECT * FROM post AS p "
                                + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location) "
                                + "AND p.id in (select i.post_id, AVG(manner_temp) from invited_users i "
                                + "GROUP BY post_id "
                                + "WHERE i.user_id in (select u.id FROM userinfo u ))", Post.class)
                        .setParameter("convertedDate1", convertedDate1);
                posts = query2.getResultList();
                break;
        }
        if (posts.size() < 1) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다"), HttpStatus.OK);
        }
        List<PostResponseDto> postList = postSearchService.searchTimeOrMannerPostList(posts, userId);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }


    //게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsDetails(Long postId, Long userId) {
        checkUser(userId);
        Post post = checkPost(postId);
        Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
        PostDetailsResponseDto postDetailsResponseDto = postSearchService.detailPost(like, post);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postDetailsResponseDto), HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<FinalResponseDto<?>> createPost(Long userId, PostRequestDto requestDto, List<MultipartFile> files) throws Exception {
        User user = checkUser(userId);
        Boolean isOwner = user.getIsOwner();
//        if (files.size() > 3) {
//            throw new PostApiException("게시글 사진은 3개 이하 입니다.");
//        }

        if (requestDto.getTags().size() > 3) {
            throw new PostApiException("최대 태그 갯수는 3개 입니다.");
//                return new ResponseEntity<>(new FinalResponseDto<>(false, "최대 태그 갯수는 3개 입니다."), HttpStatus.OK);
        }
        for (String tag : requestDto.getTags()) {
            if (tag.length() > 10) {
                throw new PostApiException("10자 이하로 태그를 입력해주세요");
//                    return new ResponseEntity<>(new FinalResponseDto<>(false, "10자 이하로 태그를 입력해주세요"), HttpStatus.OK);
            }
        }

        if(isOwner){
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 개설 실패"), HttpStatus.BAD_REQUEST);
        }else{
            user.setIsOwner(true);
        }
//        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(requestDto.getTime());
//        LocalDateTime localDateTime = LocalDateTime.now();
//        String convertedDate1 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime  PromiseDateTime = LocalDateTime.parse(requestDto.getTime(), inputFormat);
        LocalDateTime now = LocalDateTime.now();
        if(!PromiseDateTime.isAfter(now) || PromiseDateTime.isAfter(now.plusDays(1))){
            throw new PostApiException("약속시간은 현재시간 이후 부터 24시간 이내에 가능합니다.");
        }

//        String[] categoryList = new String[]{"맛집","카페","노래방","운동","친목","전시","여행","쇼핑","스터디","게임"};

        List<String> categoryList
                = new ArrayList<>(Arrays.asList("맛집", "카페", "노래방", "운동", "친목", "전시", "여행", "쇼핑", "스터디", "게임"));
        for (String categroy : requestDto.getCategories()) {
            if (!categoryList.contains(categroy)) {
                throw new PostApiException("잘못된 카테고리 입니다.");
            }
        }
        if (requestDto.getPersonnel() > 50 || requestDto.getPersonnel() < 2) {
            throw new PostApiException("참여인원은 50명 이하 입니다");
        }
        if (files == null) {
            requestDto.setPostUrls(null);
            // 기본 이미지로 변경 필요
        } else {
            List<String> postUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if(!fileExtFilter.badFileExt(file)){
                    throw new PostApiException("이미지가 아닙니다.");
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
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 개설 성공", post.getId()), HttpStatus.OK);
    }


    // 게시글 수정
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> updatePost(Long postId, Long userId, PostRequestDto requestDto, List<MultipartFile> files) throws Exception {
        checkUser(userId);
        Post post = checkPost(postId);

        List<String> postUrls = new ArrayList<>();
        if (files.size() > 0) {
            for (MultipartFile file : files) {
                postUrls.add(s3Service.upload(file));
            }
        }
        if (requestDto.getPostUrls().size()>0){
            postUrls.addAll(requestDto.getPostUrls());
        }
        requestDto.setPostUrls(postUrls);

        SearchMapDto searchMapDto = mapSearchService.findLatAndLong(requestDto.getPlace());
        Point point = mapSearchService.makePoint(searchMapDto.getLongitude(), searchMapDto.getLatitude());
        post.update(searchMapDto.getLongitude(), searchMapDto.getLatitude(), requestDto, point);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 수정 성공"), HttpStatus.OK);
    }

    public ResponseEntity<FinalResponseDto<?>> getMyPost(Long userId) {
        User user = checkUser(userId);
        Post post = postRepository.findByUserId(userId);

        if (post.getPostUrls().size() < 1) {
            post.getPostUrls().add(null);
        }
        if (!user.getIsOwner()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글을 먼저 생성해 주세요"), HttpStatus.OK);
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

        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 수정 페이지 이동 성공",postResponseDto), HttpStatus.OK);
    }


    //게시글 삭제
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> deletePost(Long postId, Long userId) {
        Post post = checkPost(postId);
        User user = checkUser(userId);
        if (!post.getUser().getId().equals(userId)) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "본인 게시글이 아닙니다."), HttpStatus.OK);
        } else {
            invitedUsersRepository.deleteAllByRoomId(post.getId().toString());
            likeRepository.deleteByPostId(postId);
            postRepository.deleteById(postId);
            user.setIsOwner(false);
            return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 삭제 성공", user.getIsOwner()), HttpStatus.OK);
        }
    }


    // 찜한 게시글 전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getLikedPosts(Long userId) {
        checkUser(userId);
        List<PostMapping> posts = likeRepository.findAllByUserIdAndIsLikeTrueOrderByPost_Id(userId);
        List<PostResponseDto> postList = postSearchService.searchLikePostList(posts, userId);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "좋아요한 게시글 조회 성공", postList), HttpStatus.OK);
    }


    //나의 번개 페이지 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getMyPage(UserDetailsImpl userDetails) {
        User user = checkUser(userDetails.getUser().getId());
        MyPageDto myPageDto = new MyPageDto(user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "나의 번개 페이지 조회 성공", myPageDto), HttpStatus.OK);
    }


    //내 벙글 확인하기
    public ResponseEntity<FinalResponseDto<?>> getMyPagePost(UserDetailsImpl userDetails) {
        User user = checkUser(userDetails.getUser().getId());
        Post post = postRepository.findByUserId(user.getId());
        Like like = likeRepository.findByUser_IdAndPost_Id(user.getId(), post.getId()).orElse(null);
        PostResponseDto postResponseDto = postSearchService.searchMyPost(like, post);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "나의 번개 페이지 조회 성공", postResponseDto), HttpStatus.OK);
    }

    // 유저 존재 여부
    public User checkUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        return user;
    }

    // 게시글 존재 여부
    public Post checkPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));
        return post;
    }


//    public double deg2rad(double deg) {
//        return (deg * Math.PI / 180.0);
//    }
//
//    public double rad2deg(double rad) {
//        return (rad * 180 / Math.PI);
//    }
//
//    //게시글 조회 (제목에 포함된 단어로)
//    public ResponseEntity<FinalResponseDto<?>> getSearch(String keyword, Long userId, Double longitude, Double latitude) {
//        Optional<User> user = userRepository.findById(userId);
//
//        if (!user.isPresent()) {
//            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 검색 실패"), HttpStatus.BAD_REQUEST);
//        }
//        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
//        LocalDateTime localDateTime = LocalDateTime.now();
//        String convertedDate1 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        Query query = em.createNativeQuery("SELECT * FROM post AS p "
//                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
//                        + " AND p.time > :convertedDate1 AND p.id in (select u.post_id from post_categories u"
//                        + " WHERE u.category in ('" + keyword + "'))"
//                        + "ORDER BY p.time", Post.class)
//                .setParameter("convertedDate1", convertedDate1);
//        List<Post> posts = query.getResultList();
//
//        if(posts.size() < 1){
//            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다, 다른단어로 검색해주세요"), HttpStatus.BAD_REQUEST);
//        }
//        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId,longitude,latitude);
//        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
//    }


    // Time 변환
//    public String timeCheck(String time) {
//        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime localDateTime = LocalDateTime.parse(time, inputFormat);
//        if (!localDateTime.isAfter(LocalDateTime.now())) {
//            Duration duration = Duration.between(localDateTime, LocalDateTime.now());
//            return duration.getSeconds() / 60 + "분 경과";
//        }
//        return localDateTime.getHour() + "시 시작 예정";
//    }

//    //pointFormat 구하기
//    public String searchPointFormat(Double distance, Double latitude, Double longitude){
//        Location northEast = GeometryUtil
//                .calculate(latitude, longitude, distance, Direction.NORTHEAST.getBearing());
//        Location southWest = GeometryUtil
//                .calculate(latitude, longitude, distance, Direction.SOUTHWEST.getBearing());
//
//        double x1 = northEast.getLatitude();
//        double y1 = northEast.getLongitude();
//        double x2 = southWest.getLatitude();
//        double y2 = southWest.getLongitude();
//        String pointFormat = String.format("'LINESTRING(%f %f, %f %f)')", x1, y1, x2, y2);
//        return pointFormat;
//    }
//
//    //카페고리및태그 리스트->스트링 변환
//    public String categoryOrTagListMergeString (List<String> categoryOrTagList){
//        String mergeList = "";
//        for (String string : categoryOrTagList) {
//            mergeList += "'" + string + "',";
//        }
//        mergeList = mergeList.substring(0, mergeList.length() - 1);
//        return mergeList;
//    }

    //postlist 찾기
//    public List<PostResponseDto> searchPostList(List<Post> posts, Long userId){
//        List<PostResponseDto> postList = new ArrayList<>();
//        for (Post post : posts) {
//            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
//            Boolean isLike;
//
//            if (like == null) {
//                isLike = false;
//            } else {
//                isLike = like.getIsLike();
//            }
//            if (post.getPostUrls().size() < 1) {
//                post.getPostUrls().add(null);
//            }
//            PostResponseDto postResponseDto = PostResponseDto.builder()
//                    .id(post.getId())
//                    .title(post.getTitle())
//                    .content(post.getContent())
//                    .personnel(post.getPersonnel())
//                    .joinCount(1)                       //TODO 수정필요
//                    .place(post.getPlace())
//                    .postUrl(post.getPostUrls().get(0)) //TODO 수정필요
//                    .time(timeCheck(post.getTime()))
//                    .avgTemp(50)                      //TODO 수정필요
//                    .isLetter(post.getIsLetter())
//                    .isLike(isLike)
//                    .build();
//            postList.add(postResponseDto);
//        }
//        return postList;
//    }
}

