package com.dowon.fluma.document.repository;

import com.dowon.fluma.document.domain.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrawingRepository extends JpaRepository<Drawing, Long> {
    Optional<Drawing> findByDocument_Id(Long id);
}
