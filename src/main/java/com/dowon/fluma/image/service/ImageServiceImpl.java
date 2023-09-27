package com.dowon.fluma.image.service;

import com.dowon.fluma.image.domain.Image;
import com.dowon.fluma.image.repo.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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

    @Override
    public void decoder(String base64, String target) throws IOException {
        String data = base64.split(",")[1];
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIO.write(image, "jpg", new File(target));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
