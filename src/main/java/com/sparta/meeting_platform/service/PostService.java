package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.PostTestDto;
import com.sparta.meeting_platform.dto.SearchMapDto;
import com.sparta.meeting_platform.dto.user.MyPageDto;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.repository.mapping.PostMapping;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private Double distance = 8.0;
    private final MapService mapService;
    private final ChatRoomRepository chatRoomRepository;


    //게시글 전체 조회(4개만)
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPosts(Long userId, Double latitude, Double longitude) {
        checkUser(userId);
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        Query query = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + "ORDER BY p.time desc", Post.class)
                .setMaxResults(4);
        List<Post> posts = query.getResultList();
        Query query1 = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + "ORDER BY p.time", Post.class)
                .setMaxResults(4);
        List<Post> posts2 = query1.getResultList();

        List<PostResponseDto> postListRealTime = postSearchService.searchPostList(posts, userId);
        List<PostResponseDto> postListEndTime = postSearchService.searchPostList(posts2, userId);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postListRealTime, postListEndTime), HttpStatus.OK);
    }// endtime, manner도 같이 보내줘야함


    //카테고리별 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsByCategories(Long userId, List<String> categories, Double latitude, Double longitude) {
        checkUser(userId);
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        String mergeList = postSearchService.categoryOrTagListMergeString(categories);
        Query query = em.createNativeQuery("SELECT * FROM post AS p "
                + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                + " AND p.id in (select u.post_id from post_categories u"
                + " WHERE u.category in (" + mergeList + "))", Post.class);
        List<Post> posts = query.getResultList();
        if (posts.size() < 1) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다, 다른 카테고리로 조회해주세요"), HttpStatus.OK);
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);
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
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }


    //게시글 더 보기 조회 추가 해야함
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> morePostList(Long userId, String status, Double latitude, Double longitude) {
        checkUser(userId);
        String pointFormat = mapSearchService.searchPointFormat(distance, latitude, longitude);
        List<Post> posts = new ArrayList<>();
        switch (status) {
            case "realTime":
                Query query = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + "ORDER BY p.time desc", Post.class);
                posts = query.getResultList();
                break;
            case "endTime":
                Query query1 = em.createNativeQuery("SELECT * FROM post AS p "
                        + "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + ", p.location)"
                        + "ORDER BY p.time", Post.class);
                posts = query1.getResultList();
                break;
        }
        List<PostResponseDto> postList = postSearchService.searchPostList(posts, userId);
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


    // 게시글 등록
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> createPost(Long userId, PostRequestDto requestDto, List<MultipartFile> files) throws Exception {
        User user = checkUser(userId);
        //        Boolean isOwner = user.getIsOwner();
//
//        if(isOwner){
//            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 개설 실패"), HttpStatus.BAD_REQUEST);
//        }else{
//            user.setIsOwner(true);
//        }
        if (files == null) {
            requestDto.setPostUrls(null);
            // 기본 이미지로 변경 필요
        } else {
            List<String> postUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                postUrls.add(s3Service.upload(file));
            }
            requestDto.setPostUrls(postUrls);
        }
        SearchMapDto searchMapDto = mapSearchService.findLatAndLong(requestDto.getPlace());
        Point point = mapSearchService.makePoint(searchMapDto.getLongitude(), searchMapDto.getLatitude());
        Post post = new Post(user, requestDto, searchMapDto.getLongitude(), searchMapDto.getLatitude(), point);
        postRepository.save(post);
        chatRoomRepository.createChatRoom(post);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 개설 성공", post.getId()), HttpStatus.OK);
    }


    // 게시글 수정
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> updatePost(Long postId, Long userId, PostRequestDto requestDto, List<MultipartFile> files) throws Exception {
        checkUser(userId);
        Post post = checkPost(postId);
        if (files == null) {
            requestDto.setPostUrls(null);
            // 기본 이미지로 변경 필요
        } else {
            List<String> postUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                postUrls.add(s3Service.upload(file));
            }
            requestDto.setPostUrls(postUrls);
        }
        SearchMapDto searchMapDto = mapSearchService.findLatAndLong(requestDto.getPlace());
        Point point = mapSearchService.makePoint(searchMapDto.getLongitude(), searchMapDto.getLatitude());
        post.update(searchMapDto.getLongitude(), searchMapDto.getLatitude(), requestDto, point);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 수정 성공"), HttpStatus.OK);
    }


    //게시글 삭제
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> deletePost(Long postId, Long userId) {
        Post post = checkPost(postId);
        User user = checkUser(userId);
        if (!post.getUser().getId().equals(userId)) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "본인 게시글이 아닙니다."), HttpStatus.OK);
        } else {
            postRepository.deleteById(postId);
            user.setIsOwner(false);
            return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 삭제 성공"), HttpStatus.OK);
        }
    }


    // 찜한 게시글 전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getLikedPosts(Long userId) {
        checkUser(userId);
        List<PostMapping> posts = likeRepository.findAllByUserIdAndIsLikeTrue(userId);
        List<PostResponseDto> postList = postSearchService.searchLikePostList(posts, userId);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "좋아요한 게시글 조회 성공", postList), HttpStatus.OK);
    }


    //나의 번개 페이지 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getMyPage(UserDetailsImpl userDetails) {
        User user = checkUser(userDetails.getUser().getId());
        MyPageDto myPageDto = new MyPageDto(user.getNickName(), user.getMannerTemp(), user.getProfileUrl(), user.getBungCount());
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
                () -> new NullPointerException("해당 유저를 찾을 수 없습니다."));
        return user;
    }

    // 게시글 존재 여부
    public Post checkPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물 입니다."));
        return post;
    }

//    public double deg2rad(double deg) {
//        return (deg * Math.PI / 180.0);
//    }
//
//    public double rad2deg(double rad) {
//        return (rad * 180 / Math.PI);
//    }


    //    //게시글 조회 (제목에 포함된 단어로)
//    public ResponseEntity<FinalResponseDto<?>> getSearch(String keyword, Long userId) throws ParseException {
//        Optional<User> user = userRepository.findById(userId);
//
//        if (!user.isPresent()) {
//            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 검색 실패"), HttpStatus.BAD_REQUEST);
//        }
//        List<PostResponseDto> postList = new ArrayList<>();
//        List<Post> posts = postRepository.findAllByTitleContainsOrderByCreatedAtDesc(keyword);
//
//        if(posts.size() < 1){
//            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다, 다른단어로 검색해주세요"), HttpStatus.BAD_REQUEST);
//        }
//
//        for (Post post : posts) {
//            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
//            Boolean isLike;
//
//            if (like == null) {
//                isLike = false;
//            } else {
//                isLike = like.getIsLike();
//            }
//            PostResponseDto postResponseDto = new PostResponseDto(post, isLike, timeCheck(post.getTime()));
//            postList.add(postResponseDto);
//        }
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

