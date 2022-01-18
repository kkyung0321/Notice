package com.example.studywithme.post.application.fileupload.interact;

import com.example.studywithme.post.application.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {

    void uploadFile(Post post, List<MultipartFile> multipartFiles) throws Exception;
}
