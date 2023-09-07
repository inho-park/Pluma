package com.dowon.fluma.document.repository;

import com.dowon.fluma.document.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
