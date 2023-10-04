package com.dowon.fluma.version.api;

import com.dowon.fluma.image.service.ImageService;
import com.dowon.fluma.version.dto.VersionDTO;
import com.dowon.fluma.version.dto.VersionPageRequestDTO;
import com.dowon.fluma.version.service.VersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/versions")
public class VersionResource {

    final private VersionService versionService;
    final private ImageService imageService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody VersionDTO dto) {
        try {
            return new ResponseEntity<>(versionService.saveVersion(dto), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity read(@PathVariable(value = "id") String versionId) {
        try {
            return new ResponseEntity<>(versionService.getVersion(Long.parseLong(versionId)), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * 해당 문서의 버전 리스트 불러오기
     *
     * @param pageRequestDTO
     * @return pageResultDTO
     */
    @GetMapping()
    public ResponseEntity getList(VersionPageRequestDTO pageRequestDTO) {
        try {
            return new ResponseEntity<>(versionService.getVersions(pageRequestDTO), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 버전 삭제하기
     * 
     * @param documentId
     * @param versionId
     * @return
     */
    @DeleteMapping(value = "/{documentId}+{versionId}")
    public ResponseEntity remove(@PathVariable(value = "documentId") String documentId,
                                 @PathVariable(value = "versionId") String versionId) {
        try {
            versionService.deleteVersion(Long.parseLong(versionId), Long.parseLong(documentId));
            return new ResponseEntity<>(imageService.deleteImageByVersion(Long.parseLong(documentId), Long.parseLong(versionId)), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
