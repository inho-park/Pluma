package com.dowon.fluma.document.repository;

import com.dowon.fluma.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query(
            "SELECT d, d.user " +
                    "FROM Document d " +
                    "WHERE d.user.id=:userId"
    )
    Page<Object[]> getDocumentsByUser_Id(Pageable pageable, @Param("userId") Long userId);


}
