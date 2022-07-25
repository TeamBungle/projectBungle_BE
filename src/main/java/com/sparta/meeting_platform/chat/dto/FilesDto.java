package com.sparta.meeting_platform.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilesDto {
     private String fileUrl;

     public FilesDto(String fileUrl) {
          this.fileUrl =fileUrl;
     }
}
