package com.dowon.fluma.image.repo;

import com.dowon.fluma.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Modifying
    @Transactional
    void deleteImagesByDocument_Id(Long documentId);
    List<Image> findAllByDocument_Id(Long documentId);
    Optional<Image> findImageByFilename(String fileName);
}
