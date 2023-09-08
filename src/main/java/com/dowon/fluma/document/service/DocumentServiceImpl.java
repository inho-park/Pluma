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
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return null;
    }

    @Override
    public PageResultDTO<DocumentDTO, Object[]> getDocuments(DocumentPageRequestDTO pageRequestDTO) {
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
