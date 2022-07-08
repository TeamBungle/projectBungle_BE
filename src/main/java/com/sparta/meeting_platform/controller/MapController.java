package com.sparta.meeting_platform.controller;


import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.MapResponseDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.MapService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MapController {

//    private final MapService mapService;
    private final MapService mapService;



    @GetMapping("/map")
    public ResponseEntity<MapResponseDto<?>> readMap(@RequestParam(value = "latitude") Double latitude,
                                                     @RequestParam(value = "longitude") Double longitude,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws java.text.ParseException {
        User user = userDetails.getUser();
       return mapService.readMap(latitude,longitude,user);
    }


    @GetMapping("/map/search")
    public ResponseEntity<MapResponseDto<?>> searchMap(@RequestParam(value = "address") String address,
                          @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws IOException, ParseException, java.text.ParseException {
        System.out.println("controller" + address);
        User user = userDetails.getUser();
        return mapService.searchMap(address,user);
    }


    @GetMapping("/map/details")
    public ResponseEntity<MapResponseDto<?>> detailsMap(@RequestParam(value = "categories",defaultValue = "") List<String> categories,
                           @RequestParam(value = "personnel",defaultValue = "") int personnel,
                           @RequestParam(value = "distance",defaultValue = "") int distance,
                           @RequestParam(value = "latitude") Double latitude,
                           @RequestParam(value = "longitude") Double longitude,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws java.text.ParseException {
        User user = userDetails.getUser();
       return mapService.detailsMap(categories,personnel,(double)distance,latitude,longitude,user);
    }
}
