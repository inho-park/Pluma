package com.dowon.fluma.image.service;

import com.dowon.fluma.image.domain.Image;
import com.dowon.fluma.image.repo.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    final private ImageRepository imageRepository;

    @Override
    public String deleteImageByDocument(Long documentId) {
        try {
            imageRepository.deleteImagesByDocument_Id(documentId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String deleteImageByVersion(Long documentId, Long VersionId) {
        try {
            List<Image> images = imageRepository.findAllByDocument_Id(documentId);
            images.forEach(i -> {
                if(i.getVersions().size() == 0) imageRepository.delete(i);
            });
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
