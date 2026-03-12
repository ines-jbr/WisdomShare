package com.wisdomshare.entity;

import lombok.*;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String firstname;
    private String lastname;
    private String email;

    public static User fromJwt(Jwt jwt) {
        return User.builder()
                .id(jwt.getSubject())
                .firstname(jwt.getClaimAsString("given_name"))
                .lastname(jwt.getClaimAsString("family_name"))
                .email(jwt.getClaimAsString("email"))
                .build();
    }

    public String getFullName() {
        return firstname + " " + lastname;
    }
}