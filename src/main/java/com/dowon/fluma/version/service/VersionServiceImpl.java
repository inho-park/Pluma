package com.dowon.fluma.version.service;

import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.version.domain.Version;
import com.dowon.fluma.version.dto.VersionDTO;
import com.dowon.fluma.version.dto.VersionPageRequestDTO;
import com.dowon.fluma.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService {

    final private VersionRepository versionRepository;
    final private DocumentRepository documentRepository;

    @Override
    public VersionDTO saveVersion(VersionDTO versionDTO) {
        Document document = documentRepository.findById(versionDTO.getDocumentId()).orElseThrow();
        return entityToDTO(versionRepository.save(dtoToEntity(versionDTO, document)), document);
    }

    @Override
    public VersionDTO getVersion(Long versionId) {
        Version version = versionRepository.findById(versionId).orElseThrow();
        return entityToDTO(version, version.getDocument());
    }

    @Override
    public PageResultDTO<VersionDTO, Object[]> getVersions(VersionPageRequestDTO pageRequestDTO) {
        Function<Object[], VersionDTO> fn = (
                entity -> entityToDTO(
                        (Version) entity[0],
                        (Document) entity[1]
                )
        );
        Page<Object[]> result;
        Long documentId = pageRequestDTO.getDocumentId();

        result = versionRepository.getVersionsByDocument_Id(
                pageRequestDTO.getPageable(Sort.by("id").descending()),
                documentId
        );
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public StatusDTO deleteVersion(Long versionId, Long documentId) {
        return null;
    }

    @Override
    public StatusDTO deleteAll(Long documentId) {
        return null;
    }

//    @Override
//    public StatusDTO updateVersion(Long versionId, VersionModifyDTO modifyDTO) {
//        return null;
//    }
}
