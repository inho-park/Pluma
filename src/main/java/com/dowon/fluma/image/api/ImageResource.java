package com.dowon.fluma.image.api;


import com.dowon.fluma.image.exception.CustomImageFormatError;
import com.dowon.fluma.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class ImageResource {
    final private ImageService imageService;


    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity upload(@PathVariable(value = "id") String documentId, MultipartFile multipartFile) throws Exception {
        try {
            return new ResponseEntity<>(imageService.addImageS3(multipartFile, Long.parseLong(documentId)), HttpStatus.OK);
        } catch (CustomImageFormatError e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
