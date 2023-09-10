package com.dowon.fluma.document.api;

import com.dowon.fluma.document.dto.DocumentDTO;
import com.dowon.fluma.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentResource {
    private final DocumentService documentService;

    /**
     * 소설 생성하기
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

}
