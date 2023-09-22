package com.dowon.fluma.image.repo;

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

    @Test
    public void 이미지_이름_생성() {
        for (int i = 1; i < 11; i++) {
            imageRepository.save(
                    Image.builder()
                            .filename(UUID.randomUUID() + "imageName" + i)
                            .build()
            );
        }
    }

    @Test
    public void 이미지_버전_연결() {
        for (int i = 1; i < 11; i++) {
            Version version = versionRepository.findById((long) i).orElseThrow();
            for (int j = 1; j < 10; j++) {
                if (i == j) continue;
                else {
                    version.getImages().add(imageRepository.findById((long) j).orElseThrow());
                }
            }
            versionRepository.save(version);
        }
    }
}
