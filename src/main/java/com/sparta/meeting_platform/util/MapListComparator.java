package com.sparta.meeting_platform.util;

import com.sparta.meeting_platform.dto.MapListDto;

import java.util.Comparator;

public class MapListComparator implements Comparator<MapListDto> {
    @Override
    public int compare(MapListDto a,MapListDto b){
        if(a.getDistance()>b.getDistance()) return 1;
        if(a.getDistance()< b.getDistance())return -1;
        return 0;
    }
}
