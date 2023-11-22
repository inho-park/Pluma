package com.dowon.fluma.document.domain;

import com.dowon.fluma.common.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;

@Log4j2
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drawing extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String fileName;

    @OneToOne
    private Document document;
}
