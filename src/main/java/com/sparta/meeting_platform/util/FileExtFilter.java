package com.sparta.meeting_platform.util;

import com.sparta.meeting_platform.exception.PostApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class FileExtFilter {

    public Boolean badFileExt (MultipartFile file){

        try{
            String fileName = file.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf(".")+1 , fileName.length()).toLowerCase();
            final String[] badExtension = {"png","jpg","jpeg", "jfif", "gif", "bmp", "img", "JPG", "PNG", "JPEG", "GIF", "BMP", "IMG"};

            for (String s : badExtension) {
                if (ext.equals(s)) {
                    return true;
                }

            }
            return false;
        } catch (NullPointerException e){
            throw new PostApiException("잘못된 형식의 파일 입니다");
        }
    }
}
