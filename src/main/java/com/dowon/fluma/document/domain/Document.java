package com.dowon.fluma.document.domain;

import com.dowon.fluma.common.domain.BaseTimeEntity;
import com.dowon.fluma.user.domain.Member;
import com.dowon.fluma.version.domain.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member user;

    public void changeTitle(String title) {
        this.title = title;
    }
}
