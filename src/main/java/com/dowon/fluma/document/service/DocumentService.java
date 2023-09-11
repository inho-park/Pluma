package com.dowon.fluma.document.service;


import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.dto.DocumentModifyDTO;
import com.dowon.fluma.document.dto.DocumentPageRequestDTO;
import com.dowon.fluma.user.domain.User;

public interface DocumentService {
    DocumentDTO saveDocument(DocumentDTO documentDTO);
    DocumentDTO getDocument(Long documentId);
    PageResultDTO<DocumentDTO, Object[]> getDocuments(DocumentPageRequestDTO pageRequestDTO);
    StatusDTO deleteDocument(Long documentId, String username);
    StatusDTO updateDocument(Long documentId, DocumentModifyDTO modifyDTO);
    default Document dtoToEntity(DocumentDTO dto, User user) {
        return Document.builder()
                .title(dto.getTitle())
                .user(user)
                .build();
    }

    default DocumentDTO entityToDTO(Document document, User user) {
        return DocumentDTO.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .username(user.getUsername())
                .modDate(document.getModDate())
                .regDate(document.getRegDate())
                .build();
    }
}
