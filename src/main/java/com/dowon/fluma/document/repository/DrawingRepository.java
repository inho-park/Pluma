package com.dowon.fluma.document.repository;

import com.dowon.fluma.document.domain.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawingRepository extends JpaRepository<Drawing, Long> {

}
