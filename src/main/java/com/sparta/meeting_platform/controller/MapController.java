package com.sparta.meeting_platform.controller;


import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MapController {

//    private final MapService mapService;
    private final MapService mapService;



    @GetMapping("/map")
    public void readMap(@RequestParam(value = "latitude") Double latitude,
                        @RequestParam(value = "longitude") Double longitude,
                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        mapService.readMap(latitude,longitude,user);
    }

//    @GetMapping("/map/search")
//    public void searchMap(@RequestParam(value = "address") String address) {
//        System.out.println("controller" + address);
//        mapService.searchMap(address);
//    }

    @GetMapping("/map/details")
    public void detailsMap(@RequestParam(value = "categories",defaultValue = "") List<String> categories,
                           @RequestParam(value = "joinCount",defaultValue = "") int joinCount,
                           @RequestParam(value = "distance",defaultValue = "") int distance,
                           @RequestParam(value = "latitude") Double latitude,
                           @RequestParam(value = "longitude") Double longitude,
                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        mapService.detailsMap(categories,joinCount,distance,latitude,longitude,user);
    }
}
