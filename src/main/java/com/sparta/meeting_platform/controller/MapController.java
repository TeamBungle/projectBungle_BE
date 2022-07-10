package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.dto.MapResponseDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.MapService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/map")
public class MapController {

    private final MapService mapService;


    //번개지도 화면 조회
    @GetMapping("")
    public ResponseEntity<MapResponseDto<?>> readMap(@RequestParam(value = "latitude") Double latitude,
                                                     @RequestParam(value = "longitude") Double longitude,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
       return mapService.readMap(latitude,longitude,userId);
    }

    //번개 지도 주소 검색 결과 조회
    @GetMapping("/search")
    public ResponseEntity<MapResponseDto<?>> searchMap(@RequestParam(value = "address") String address,
                          @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws IOException, ParseException {
        System.out.println("controller" + address);
        Long userId = userDetails.getUser().getId();
        return mapService.searchMap(address,userId);
    }

    //번개지도 화면 세부 설정 조회
    @GetMapping("/details")
    public ResponseEntity<MapResponseDto<?>> detailsMap(@RequestParam(value = "categories",defaultValue = "") List<String> categories,
                           @RequestParam(value = "personnel",defaultValue = "") int personnel,
                           @RequestParam(value = "distance",defaultValue = "",required = false) int distance,
                           @RequestParam(value = "latitude") Double latitude,
                           @RequestParam(value = "longitude") Double longitude,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
       return mapService.detailsMap(categories,personnel,(double)distance,latitude,longitude,userId);
    }
}
