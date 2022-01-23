package com.example.studywithme.imagefile.application.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFileResponse {
    private Long ifId;

    private String path;
}
