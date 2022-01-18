package com.example.studywithme.imagefile.application.dao;

import com.example.studywithme.imagefile.application.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ImageFile imf where imf.post.pid = :pid")
    void deleteAllByPid(Long pid);
}
