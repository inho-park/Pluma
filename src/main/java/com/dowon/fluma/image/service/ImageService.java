package com.dowon.fluma.image.service;

import com.dowon.fluma.image.domain.Image;

public interface ImageService {
    String deleteImageByDocument(Long documentId);

    String deleteImageByVersion(Long documentId, Long VersionId);
}
