package com.dowon.fluma.version.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
public class VersionPageRequestDTO {

    private Long documentId;
    private int page;
    private int size;

    public VersionPageRequestDTO() {
        this.page = 1;
        this.size = 100;
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
}
