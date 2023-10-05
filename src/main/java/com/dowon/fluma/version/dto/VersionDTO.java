package com.dowon.fluma.version.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VersionDTO {
    private Long versionId;
    private String subtitle;
    private String content;
    private LocalDateTime createdAt;
    private Long documentId;
    private String[] filePaths;
}
