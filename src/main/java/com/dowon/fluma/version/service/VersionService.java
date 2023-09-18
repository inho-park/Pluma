package com.dowon.fluma.version.service;

import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.version.domain.Version;
import com.dowon.fluma.version.dto.VersionDTO;
import com.dowon.fluma.version.dto.VersionListDTO;
import com.dowon.fluma.version.dto.VersionPageRequestDTO;

public interface VersionService {
    VersionDTO saveVersion(VersionDTO versionDTO);
    VersionDTO getVersion(Long versionId);
    PageResultDTO<VersionListDTO, Object[]> getVersions(VersionPageRequestDTO pageRequestDTO);
    StatusDTO deleteVersion(Long versionId, Long documentId);
    StatusDTO deleteAll(Long documentId);
//    StatusDTO updateVersion(Long versionId, VersionModifyDTO modifyDTO);

    default Version dtoToEntity(VersionDTO versionDTO, Document document) {
        return Version.builder()
                .subtitle(versionDTO.getSubtitle())
                .content(versionDTO.getContent())
                .document(document)
                .build();
    }

    default VersionDTO entityToDTO(Version version, Document document) {
        return VersionDTO.builder()
                .subtitle(version.getSubtitle())
                .content(version.getContent())
                .createdAt(version.getCreatedAt())
                .versionId(version.getId())
                .documentId(document.getId())
                .build();
    }

}
