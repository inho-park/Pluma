package com.dowon.fluma.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false, length = 100)
    private String username;

    @Column
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column
    private String provider;

    @Column
    private String providerId;

}
