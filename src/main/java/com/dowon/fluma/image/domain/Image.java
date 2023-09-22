package com.dowon.fluma.image.domain;

import com.dowon.fluma.version.domain.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
//1. 클라이언트 img api 요청 ( base64 로 암호화된 상태로 받음 )
//2. 클라이언트에서 버전 저장시 암호화된 상태 그대로 백에 전달
//3. 백은 해당 encoded text 를 복호화하여 s3 에 uuid 로 이름을 부여하여 저장
//4. 해당 이름( uuid )를 저장하는 image 테이블을 생성하여 version 테이블과 다대다 관계를 설정하여 참조테이블 생성
//
//    결론
//1. 한 번 저장된 버전은 수정 불가능, 삭제 가능
//2. 즉 한 번 저장된 이미지는 수정 필요 X, 삭제 필요 O
//3. 이미지가 삭제되는 순간은 해당 버전이 삭제되는 순간 혹은 문서 자체가 삭제되는 순간

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, unique = true, nullable = false)
    private String filename;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "image_versions",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "version_id"))
    private List<Version> versions = new ArrayList<>();


}
