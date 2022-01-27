package com.example.studywithme.post.application.fileupload.interact.impl;

import com.example.studywithme.imagefile.application.interact.ImageFileService;
import com.example.studywithme.post.application.entity.Post;
import com.example.studywithme.post.application.fileupload.interact.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final ImageFileService imageFileService;

    @Override
    public void uploadFile(Post post, List<MultipartFile> multipartFiles) throws Exception {
        List<MultipartFile> multipartFileList = Optional.ofNullable(multipartFiles).orElseThrow();

        for (MultipartFile file : multipartFileList) {
            String path = uploadFileAndReturnFilePath(file);

            imageFileService.saveImageFile(post1, path);
        }
    }

    private String uploadFileAndReturnFilePath(MultipartFile file) throws IOException {
        MultipartFile file1 = Optional.ofNullable(file).orElseThrow();

        String prefix = file1.getOriginalFilename()
                .substring(file1.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + prefix;
        String path = getUploadPath() + filename;

        File dest = new File(path);
        file.transferTo(dest);
        return path;
    }

    private String getUploadPath() {
        String uploadPath = "/home/kiyoung/studyWithMe/upload-file/";
        makeUploadPathFolder(uploadPath);

        return uploadPath;
    }

    private void makeUploadPathFolder(String uploadPath) {
        File folder = new File(uploadPath);
        if (!folder.isDirectory())
            folder.mkdirs();
    }
}
