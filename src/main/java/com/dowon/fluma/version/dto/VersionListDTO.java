package com.dowon.fluma.version.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionListDTO {
    private Long id;
    private LocalDateTime createdAt;
    private String subtitle;

    static public VersionListDTO entityToDTO(Long versionId, LocalDateTime createdAt, String subtitle) {
        return VersionListDTO.builder()
                .id(versionId)
                .createdAt(createdAt)
                .subtitle(subtitle)
                .build();
    }
}
