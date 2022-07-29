package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.MapDto.MapListDto;
import com.sparta.meeting_platform.dto.MapDto.MapResponseDto;
import com.sparta.meeting_platform.dto.MapDto.SearchMapDto;
import com.sparta.meeting_platform.exception.MapApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MapService {
    private final EntityManager em;
    private final PostSearchService postSearchService;
    private final MapSearchService mapSearchService;
    private final UserRepository userRepository;
    private final Double distance = 400000.0;


    //지도탭 입장
    @Transactional(readOnly = true)
    public ResponseEntity<MapResponseDto<?>> readMap(Double latitude, Double longitude, Long userId)
            throws org.locationtech.jts.io.ParseException {
        User user = checkUser(userId);

        Query query = em.createNativeQuery(
                        "SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "ORDER BY distance", Post.class)
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distance);
        List<Post> posts = query.getResultList();

        if (posts.size() < 1) {
            throw new MapApiException(distance + "km 내에 모임이 존재하지 않습니다.");
        }
        List<MapListDto> mapListDtos = postSearchService.searchMapPostList(posts, userId, longitude, latitude);

        return new ResponseEntity<>(
                new MapResponseDto<>(
                        true, "50km 내에 위치한 모임", mapListDtos, user.getIsOwner()), HttpStatus.OK);
    }

    // 주소 검색 결과
    @Transactional(readOnly = true)
    public ResponseEntity<MapResponseDto<?>> searchMap(String address, Long userId)
            throws IOException, ParseException, org.locationtech.jts.io.ParseException {
        User user = checkUser(userId);
        SearchMapDto searchMapDto = mapSearchService.findLatAndLong(address);

        Query query = em.createNativeQuery(
                        "SELECT *,ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) AS distance FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "ORDER BY distance", Post.class)
                .setParameter("myPoint",
                        mapSearchService.makePoint(searchMapDto.getLongitude(), searchMapDto.getLatitude()))
                .setParameter("distance", distance);
        List<Post> posts = query.getResultList();

        if (posts.size() < 1) {
            throw new MapApiException(distance + "km 내에 모임이 존재하지 않습니다.");
        }
        List<MapListDto> mapListDtos
                = postSearchService.searchMapPostList(posts, userId, searchMapDto.getLongitude(), searchMapDto.getLatitude());

        return new ResponseEntity<>(
                new MapResponseDto<>(
                        true, "50km 내에 위치한 모임", mapListDtos, user.getIsOwner()), HttpStatus.OK);
    }

    //지도 세부 설정 검색
    @Transactional(readOnly = true)
    public ResponseEntity<MapResponseDto<?>> detailsMap(List<String> categories, int personnel, Double distance,
                                                        Double latitude, Double longitude, Long userId)
            throws org.locationtech.jts.io.ParseException {
        User user = checkUser(userId);
        Double distanceKm = distance * 1000;
        String mergeList = postSearchService.categoryOrTagListMergeString(categories);

        Query query = em.createNativeQuery(
                        "SELECT id, content, created_at, is_letter, latitude, location, longitude,"
                                + "modified_at, personnel, place, time, title, user_id , "
                                + "ROUND(ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude))) AS 'distance' "
                                + "FROM post AS p "
                                + "WHERE ST_DISTANCE_SPHERE(:myPoint, POINT(p.longitude, p.latitude)) < :distance "
                                + "AND personnel <= " + personnel + " AND p.id in (select u.post_id from post_categories u "
                                + "WHERE u.category in (" + mergeList + "))"
                                + "ORDER BY distance", Post.class)
                .setParameter("myPoint", mapSearchService.makePoint(longitude, latitude))
                .setParameter("distance", distanceKm);
        List<Post> posts = query.getResultList();

        List<MapListDto> mapListDtos = postSearchService.searchMapPostList(posts, userId, longitude, latitude);

        return new ResponseEntity<>(
                new MapResponseDto<>(
                        true, "세부 조회 성공!!", mapListDtos, user.getIsOwner()), HttpStatus.OK);
    }

    public User checkUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        return user;
    }
}
