package com.dowon.fluma.oauth.domain;

import javax.persistence.*;

public class OAuthMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;
}
