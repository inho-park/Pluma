package com.dowon.fluma.image.api;

import com.dowon.fluma.image.service.ImageService;
import com.dowon.fluma.version.service.VersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageResource {
    final private ImageService imageService;
    final private VersionService versionService;

}
