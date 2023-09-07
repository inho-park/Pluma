package com.dowon.fluma.version.repository;

import com.dowon.fluma.version.domain.Version;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<Version, Long> {

}
