package com.example.studywithme.imagefile.application.interact.impl;

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
        Post post1 = Optional.ofNullable(post).orElseThrow();

        String path1 = Optional.ofNullable(path).orElseThrow();

        ImageFile imageFile = createImageFile(post1, path1);

        imageFileRepository.save(imageFile);
    }

    @Override
    public void deletePostAssociatedImageFiles(Long pid) {
        imageFileRepository.deleteAllByPid(pid);
    }

    private ImageFile createImageFile(Post post, String path) {
        ImageFile imageFile = ImageFile.builder()
                .path(path)
                .build();

        post.updateImageFile(imageFile);

        return imageFile;
    }
}
