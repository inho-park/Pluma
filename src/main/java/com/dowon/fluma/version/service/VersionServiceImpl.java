package com.dowon.fluma.version.service;

import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.image.domain.Image;
import com.dowon.fluma.image.repo.ImageRepository;
import com.dowon.fluma.version.domain.Version;
import com.dowon.fluma.version.dto.VersionDTO;
import com.dowon.fluma.version.dto.VersionListDTO;
import com.dowon.fluma.version.dto.VersionPageRequestDTO;
import com.dowon.fluma.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService {

    final private VersionRepository versionRepository;
    final private ImageRepository imageRepository;
    final private DocumentRepository documentRepository;

    @Override
    public Long saveVersion(VersionDTO versionDTO) {
        Document document = documentRepository.findById(versionDTO.getDocumentId()).orElseThrow();
        return versionRepository.save(dtoToEntity(versionDTO, document)).getId();
    }

    @Override
    public VersionDTO getVersion(Long versionId) {
        Version version = versionRepository.findById(versionId).orElseThrow();
        List<Image> list = imageRepository.findALlByVersionsContaining(versionId);
        String filePaths[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            filePaths[i] = list.get(i).getFilename();
        }
        return entityToDTO(version, version.getDocument(), filePaths);
    }

    @Override
    public PageResultDTO<VersionListDTO, Object[]> getVersions(VersionPageRequestDTO pageRequestDTO) {
        Function<Object[], VersionListDTO> fn = (
                entity -> VersionListDTO.entityToDTO(
                        (Long) entity[0],
                        (LocalDateTime) entity[1],
                        (String) entity[2]
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
        Document document = documentRepository.findById(documentId).orElseThrow();
        versionRepository.delete(
                Version.builder()
                        .id(versionId)
                        .document(document)
                        .build()
        );
        return StatusDTO.builder().status("success").build();
    }

    @Override
    public StatusDTO deleteAll(Long documentId) {
        versionRepository.deleteAllByDocument_Id(documentId);
        return StatusDTO.builder().status("success").build();
    }

}
