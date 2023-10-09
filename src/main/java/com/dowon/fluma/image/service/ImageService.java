package com.dowon.fluma.image.service;

import com.dowon.fluma.version.dto.VersionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    String deleteImageByDocument(Long documentId);

    String deleteImageByVersion(Long documentId, Long VersionId);

    String addImageS3(MultipartFile multipartFile, Long documentId) throws IOException;

    void addImage(String uuid, Long documentId);

    void deleteImageS3(String fileName);

    Long linkImagesWithVersion(List<String> filePaths, Long versionId);

    void deleteImageWithoutVersions();
}
