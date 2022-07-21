package com.sparta.meeting_platform.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class FileExtFilter {

    public Boolean badFileExt (MultipartFile file){
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1 , fileName.length());
        final String[] badExtension = {"png","jpg","jpeg"};

        for (String s : badExtension) {
            if (ext.equals(s)) {
                return true;
            }

        }
        return false;

    }
}
