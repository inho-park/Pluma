package com.dowon.fluma.document.service;

import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.dto.DocumentModifyDTO;
import com.dowon.fluma.document.dto.DocumentPageRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    @Override
    public DocumentDTO saveDocument(DocumentDTO documentDTO) {
        return null;
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
