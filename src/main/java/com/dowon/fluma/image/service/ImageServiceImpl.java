package com.dowon.fluma.image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.image.domain.Image;
import com.dowon.fluma.image.repo.ImageRepository;
import com.dowon.fluma.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    final private ImageRepository imageRepository;
    final private DocumentRepository documentRepository;
    final private VersionRepository versionRepository;
    final private AmazonS3Client amazonS3Client;
    @Value(value = "${cloud.aws.s3.bucket}")
    private String bucket;



    @Override
    public String deleteImageByDocument(Long documentId) {
        try {
            List<Image> images = imageRepository.findAllByDocument_Id(documentId);
            images.forEach(i -> deleteImageS3(i.getFilename()));
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
                if(i.getVersions().size() == 0) {
                    deleteImageS3(i.getFilename());
                    imageRepository.delete(i);
                }
            });
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String addImageS3(MultipartFile multipartFile, Long documentId) throws IOException {
        String fileName = UUID.randomUUID().toString() + multipartFile.getOriginalFilename(); // 파일 이름
        long size = multipartFile.getSize(); // 파일 크기

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(size);

        // S3에 업로드
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        addImage(fileName, documentId);
        String imagePath = amazonS3Client.getUrl(bucket, fileName).toString(); // 접근가능한 URL 가져오기
        return imagePath;
    }

    @Override
    public void addImage(String uuid, Long documentId) {
        imageRepository.save(Image.builder()
                        .document(documentRepository.findById(documentId).orElseThrow())
                        .filename(uuid)
                .build()
        );
    }

    @Override
    public void deleteImageS3(String fileName) {
        try {
           amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String linkImagesWithVersion(String[] fileNames, Long versionId) {
        for (String fileName: fileNames) {
            if(!imageRepository.findImageByFilename(fileName).isEmpty()) {
                Image image = imageRepository.findImageByFilename(fileName).orElseThrow();
                image.getVersions().add(versionRepository.getReferenceById(versionId));
                imageRepository.save(image);
            }
        }
        return "success";
    }


}
