package com.dowon.fluma.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(unique = true, nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String value;

    public RefreshToken updateValue(String newToken) {
        this.value = newToken;
        return this;
    }
}
