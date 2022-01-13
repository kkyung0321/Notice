package com.example.studywithme.imagefile.application.interact.impl;

import com.example.studywithme.global.error.exception.ErrorCode;
import com.example.studywithme.global.error.exception.InvalidValueException;
import com.example.studywithme.imagefile.application.dao.ImageFileRepository;
import com.example.studywithme.imagefile.application.entity.ImageFile;
import com.example.studywithme.imagefile.application.interact.ImageFileService;
import com.example.studywithme.post.application.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class ImageFileServiceImpl implements ImageFileService {

    private final ImageFileRepository imageFileRepository;

    public void saveImageFile(Post post, String path) {

        imageFileRepository.save(createImageFile(getPost(post), getPath(path)));
    }

    private ImageFile createImageFile(Post post, String path) {

        ImageFile imageFile = ImageFile.builder()
                .path(path)
                .build();

        imageFile.associateWithPost(post);

        return imageFile;
    }

    private String getPath(String path) {
        return Optional.ofNullable(path).orElseThrow(() -> {
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        });
    }

    private Post getPost(Post post) {
        return Optional.ofNullable(post).orElseThrow(() -> {
            throw new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        });
    }
}
