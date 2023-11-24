package com.dowon.fluma.document.service;


import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.domain.Drawing;
import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.dto.DocumentModifyDTO;
import com.dowon.fluma.document.dto.DocumentPageRequestDTO;
import com.dowon.fluma.user.domain.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface DocumentService {
    DocumentDTO saveDocument(DocumentDTO documentDTO);
    DocumentDTO getDocument(Long documentId);
    PageResultDTO<DocumentDTO, Object[]> getDocuments(DocumentPageRequestDTO pageRequestDTO);
    StatusDTO deleteDocument(Long documentId, Long userId);
    StatusDTO updateDocument(Long documentId, DocumentModifyDTO modifyDTO);
    String addImageS3(Long documentId, String filePath) throws IOException;
    public void addImage(String fileName, Long documentId);
    public void deleteImage(Long documentId);
    default Document dtoToEntity(DocumentDTO dto, Member user) {
        return Document.builder()
                .title(dto.getTitle())
                .user(user)
                .build();
    }

    default DocumentDTO entityToDTO(Document document, Member user) {
        return DocumentDTO.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .userId(user.getId())
                .modDate(document.getModDate())
                .regDate(document.getRegDate())
                .build();
    }
}
