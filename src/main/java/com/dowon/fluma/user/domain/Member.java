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

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    private Authority authority;
}
