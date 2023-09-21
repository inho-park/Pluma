package com.dowon.fluma.image.repo;

import com.dowon.fluma.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    
}
