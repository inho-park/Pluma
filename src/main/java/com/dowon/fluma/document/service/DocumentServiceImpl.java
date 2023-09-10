package com.dowon.fluma.document.service;

import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.dto.DocumentModifyDTO;
import com.dowon.fluma.document.dto.DocumentPageRequestDTO;
import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.user.domain.User;
import com.dowon.fluma.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;


@Log4j2
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Override
    public DocumentDTO saveDocument(DocumentDTO documentDTO) {
        User user = userRepository.findByName(documentDTO.getName()).orElseThrow();
        Document document = dtoToEntity(documentDTO, user);
        documentRepository.save(document);
        return entityToDTO(document, user);
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
                        (User) entity[1])
                );
        Page<Object[]> result;
        String name = pageRequestDTO.getName();

        return null;
    }

    @Override
    public StatusDTO deleteDocument(Long documentId, String username) {
        return null;
    }

    @Override
    public StatusDTO updateDocument(Long documentId, DocumentModifyDTO modifyDTO) {
        return null;
    }
}
