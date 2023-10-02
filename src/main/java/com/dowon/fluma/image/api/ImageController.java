package com.dowon.fluma.image.api;

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
@RequestMapping("/test")
public class ImageController {
    // image 를 삭제하는 상황은 딱 2개의 상황에서만 발생
    // 1. version 삭제 시 version 에 있던 image 중 다른 version 에서 사용하지 않는 image 삭제
    // 2. document 삭제 시 해당 document 에 포함된 version 들이 가진 모든 image 삭제
    //
    // 즉 해당 이미지를 저장할 때 version 뿐만이 아니라 document 에
    @PostMapping(value = "/image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(
            @RequestPart(value = "file")MultipartFile multipartFile,
            @PathVariable("id") String id) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
