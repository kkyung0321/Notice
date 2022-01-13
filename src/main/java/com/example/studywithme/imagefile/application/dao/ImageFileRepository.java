package com.example.studywithme.imagefile.application.dao;

import com.example.studywithme.imagefile.application.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
