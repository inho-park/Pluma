package com.dowon.fluma.document.service;

import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.dto.DocumentModifyDTO;
import com.dowon.fluma.document.dto.DocumentPageRequestDTO;
import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.image.repo.ImageRepository;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.repository.MemberRepository;
import com.dowon.fluma.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;


@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    final private MemberRepository userRepository;
    final private DocumentRepository documentRepository;
    final private ImageRepository imageRepository;
    final private VersionRepository versionRepository;

    @Override
    public DocumentDTO saveDocument(DocumentDTO documentDTO) {
        Member user = userRepository.findById(documentDTO.getUserId()).orElseThrow();
        return entityToDTO(documentRepository.save(dtoToEntity(documentDTO, user)), user);
    }

    @Override
    public DocumentDTO getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow();
        return entityToDTO(document, document.getUser());
    }

    @Override
    public PageResultDTO<DocumentDTO, Object[]> getDocuments(DocumentPageRequestDTO pageRequestDTO) {
        Function<Object [], DocumentDTO> fn = (
                entity -> entityToDTO(
                        (Document) entity[0],
                        (Member) entity[1])
                );
        Page<Object[]> result;
        Long userId = pageRequestDTO.getUserId();
        if (userRepository.existsById(userId)) {
            result = documentRepository.getDocumentsByUser_Id(
                    pageRequestDTO.getPageable(Sort.by("id").descending()),
                    userId
            );
            return new PageResultDTO<>(result, fn);
        } else  {
            throw new RuntimeException("로그인 필요");
        }
    }

    @Override
    public StatusDTO deleteDocument(Long documentId, Long userId) {
        versionRepository.deleteAllByDocument_Id(documentId);
        Member user = userRepository.findById(userId).orElseThrow();
        documentRepository.delete(
            Document.builder()
                    .id(documentId)
                    .user(user)
                    .build()
        );
        return StatusDTO.builder().status("success").build();
    }

    @Override
    public StatusDTO updateDocument(Long documentId, DocumentModifyDTO modifyDTO) {
        Document document = documentRepository.findById(documentId).orElseThrow();
        document.changeTitle(modifyDTO.getTitle());
        documentRepository.save(document);
        return StatusDTO.builder().status("success").build();
    }
}
