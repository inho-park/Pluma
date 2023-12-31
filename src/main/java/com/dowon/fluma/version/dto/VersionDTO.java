package com.dowon.fluma.version.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class VersionDTO {
    private Long versionId;
    private String subtitle;
    private String content;
    private LocalDateTime createdAt;
    private Long documentId;
    private List<String> filePaths;

    public VersionDTO() {
        filePaths = new ArrayList<>();
    }
}
