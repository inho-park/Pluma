package com.dowon.fluma.version.repository;

import com.dowon.fluma.version.domain.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VersionRepository extends JpaRepository<Version, Long> {
    @Query(
            "SELECT v, v.document " +
                    "FROM Version v " +
                    "WHERE v.document.id=:documentId"
    )
    Page<Object[]> getVersionsByDocument_Id(Pageable pageable, @Param("documentId") Long documentId);
}
