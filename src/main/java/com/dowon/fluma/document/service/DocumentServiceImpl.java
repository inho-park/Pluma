package com.dowon.fluma.document.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dowon.fluma.common.dto.PageResultDTO;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.domain.Drawing;
import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.dto.DocumentModifyDTO;
import com.dowon.fluma.document.dto.DocumentPageRequestDTO;
import com.dowon.fluma.document.exception.CustomNoSuchDocumentException;
import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.document.repository.DrawingRepository;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.user.repository.MemberRepository;
import com.dowon.fluma.version.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;


@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    final private MemberRepository userRepository;
    final private DocumentRepository documentRepository;
    final private DrawingRepository drawingRepository;
    final private VersionRepository versionRepository;
    final private AmazonS3Client amazonS3Client;
    @Value(value = "${cloud.aws.s3.bucket}")
    private String bucket;
    @Value(value = "${UPLOAD_PATH}")
    private String uploadPath;

    @Override
    public DocumentDTO saveDocument(DocumentDTO documentDTO) {
        Member user = userRepository.findById(documentDTO.getUserId()).orElseThrow();
        return entityToDTO(documentRepository.save(dtoToEntity(documentDTO, user)), user);
    }

    @Override
    public DocumentDTO getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(CustomNoSuchDocumentException::new);
        DocumentDTO dto = entityToDTO(document, document.getUser());
        String fileName = "";
        Optional<Drawing> option = drawingRepository.findByDocument_Id(documentId);
        dto.setFileName(option.isPresent()?option.get().getFileName():"");
        return dto;
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
            PageResultDTO<DocumentDTO, Object[]> resultDTO = new PageResultDTO<>(result, fn);
            resultDTO.getDtoList().forEach(
                    i -> {
                        Optional<Drawing> drawing = drawingRepository.findByDocument_Id(i.getDocumentId());
                        drawing.ifPresent(value -> i.setFileName(value.getFileName()));
                    }
            );
            return resultDTO;
        } else  {
            throw new RuntimeException("로그인 필요");
        }
    }

    @Override
    public StatusDTO deleteDocument(Long documentId, Long userId) {
        versionRepository.deleteAllByDocument_Id(documentId);
        Member user = userRepository.findById(userId).orElseThrow();
        Drawing drawing = drawingRepository.findByDocument_Id(documentId).orElseThrow();
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket + "/drawing", drawing.getFileName()));
        drawingRepository.deleteByDocument_Id(documentId);
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

    @Override
    public String addImageS3(Long documentId, String filePath) throws IOException {
        // 기존의 이미지가 있을 시 삭제 후 새로 등록
        if (drawingRepository.existsByDocument_Id(documentId)) deleteImage(documentId);
        
        String extension = "png";
        String fileName = UUID.randomUUID().toString() + "." + extension;
        String OUTPUT_FILE_PATH = uploadPath + fileName;
        try(InputStream in = new URL(filePath).openStream()){
            Path imagePath = Paths.get(OUTPUT_FILE_PATH);
            Files.copy(in, imagePath);
            long size = Files.size(imagePath);
            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType(Files.probeContentType(imagePath));
            objectMetaData.setContentLength(size);
            // S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket + "/drawing", fileName, new FileInputStream(OUTPUT_FILE_PATH), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            Files.delete(imagePath);
            addImage(fileName, documentId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public void addImage(String fileName, Long documentId) {
        drawingRepository.save(
                Drawing.builder()
                        .fileName(fileName)
                        .document(documentRepository.findById(documentId).orElseThrow())
                        .build()
        );
    }

    @Override
    public void deleteImage(Long documentId) {
        drawingRepository.deleteByDocument_Id(documentId);
        String fileName = drawingRepository.findByDocument_Id(documentId).orElseThrow().getFileName();
        amazonS3Client.deleteObject(bucket + "/drawing", fileName);
    }
}
