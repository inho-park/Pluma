package com.dowon.fluma.version.service;

import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.image.domain.Image;
import com.dowon.fluma.version.domain.Version;
import com.dowon.fluma.version.dto.VersionDTO;
import com.dowon.fluma.version.dto.VersionListDTO;
import com.dowon.fluma.version.dto.VersionPageRequestDTO;

import java.util.List;

public interface VersionService {
    Long saveVersion(VersionDTO versionDTO);
    VersionDTO getVersion(Long versionId);
    PageResultDTO<VersionListDTO, Object[]> getVersions(VersionPageRequestDTO pageRequestDTO);
    StatusDTO deleteVersion(Long versionId, Long documentId);
    StatusDTO deleteAll(Long documentId);
//    StatusDTO updateVersion(Long versionId, VersionModifyDTO modifyDTO);

    default Version dtoToEntity(VersionDTO versionDTO, Document document, List<Image> imageList) {
        return Version.builder()
                .subtitle(versionDTO.getSubtitle())
                .content(versionDTO.getContent())
                .images(imageList)
                .document(document)
                .build();
    }

    default VersionDTO entityToDTO(Version version, Document document, List<String> filePaths) {
        return VersionDTO.builder()
                .subtitle(version.getSubtitle())
                .content(version.getContent())
                .filePaths(filePaths)
                .createdAt(version.getCreatedAt())
                .versionId(version.getId())
                .documentId(document.getId())
                .build();
    }

}
