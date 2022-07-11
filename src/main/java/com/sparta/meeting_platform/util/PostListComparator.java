package com.sparta.meeting_platform.util;

import com.sparta.meeting_platform.dto.MapListDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;

import java.util.Comparator;

public class PostListComparator implements Comparator<PostResponseDto> {
    @Override
    public int compare(PostResponseDto a, PostResponseDto b){
        if(a.getDistance()>b.getDistance()) return 1;
        if(a.getDistance()< b.getDistance())return -1;
        return 0;
    }
}
