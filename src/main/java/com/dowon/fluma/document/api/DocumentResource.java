package com.dowon.fluma.document.api;

import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.dto.DocumentModifyDTO;
import com.dowon.fluma.document.dto.DocumentPageRequestDTO;
import com.dowon.fluma.document.service.DocumentService;
import com.dowon.fluma.image.exception.CustomImageFormatError;
import com.dowon.fluma.image.service.ImageService;
import com.dowon.fluma.version.service.VersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentResource {
    private final DocumentService documentService;
    private final VersionService versionService;
    private final ImageService imageService;

    /**
     * 문서 생성하기
     *
     * @param dto
     * @return ResponseEntity
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody DocumentDTO dto) {
        try {
            return new ResponseEntity<>(documentService.saveDocument(dto), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity read(@PathVariable(value = "id") String documentId) {
        try {
            return new ResponseEntity<>(documentService.getDocument(Long.parseLong(documentId)), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * 내 문서 리스트 불러오기
     *
     * @param pageRequestDTO
     * @return pageResultDTO
     */
    @GetMapping()
    public ResponseEntity getList(DocumentPageRequestDTO pageRequestDTO) {
        try {
            return new ResponseEntity<>(documentService.getDocuments(pageRequestDTO), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 내 문서 수정하기
     *
     * @param id
     * @param dto
     * @return
     */
    @PutMapping(value= "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity modify(@PathVariable(value = "id") String id, @RequestBody DocumentModifyDTO dto) {
        try {
            return new ResponseEntity<>(documentService.updateDocument(Long.parseLong(id), dto), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 내 문서에 이미지 추가하기
     * 
     * @param id
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/drawing/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity addDrawing(@PathVariable(value = "id") String id, MultipartFile multipartFile) throws Exception {
        try {
            return new ResponseEntity<>(documentService.addImageS3(Long.parseLong(id), multipartFile), HttpStatus.OK);
        } catch (CustomImageFormatError e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 소설 삭제하기
     *
     * @param userId
     * @param documentId
     * @return statusDTO
     */
    @DeleteMapping(value = "/{userId}+{documentId}")
    public ResponseEntity delete(@PathVariable(value = "userId") String userId, @PathVariable(value = "documentId") String documentId) {
        try {
            versionService.deleteAll(Long.parseLong(documentId));
            imageService.deleteImageByDocument(Long.parseLong(documentId));
            return new ResponseEntity<>(documentService.deleteDocument(Long.parseLong(documentId), Long.parseLong(userId)), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
