package com.dowon.fluma.image.repo;

import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.image.domain.Image;
import com.dowon.fluma.version.domain.Version;
import com.dowon.fluma.version.repository.VersionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class ImageRepositoryTests {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @Test
    public void 이미지_이름_생성() {
        for (int i = 1; i < 101; i++) {
            imageRepository.save(
                    Image.builder()
                            .filename(UUID.randomUUID() + "imageName" + i)
                            .document(documentRepository.findById((long)1).orElseThrow())
                            .build()
            );
        }
    }

    @Test
    public void 이미지_버전_연결() {
        for (int k = 0; k < 10; k++)
            for (int i = 0; i < 10; i++) {
                Version version = versionRepository.findById(k * 10 + (long)i + 1).orElseThrow();
                for (int j = 1; j < 11; j++) {
                    if (i + 1== j) continue;
                    else version.getImages().add(imageRepository.findById(k * 10 + (long)j).orElseThrow());
                }
                versionRepository.save(version);
            }
    }
}
