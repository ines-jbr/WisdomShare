package com.wisdomshare.controller;

import com.wisdomshare.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/welcome-email")
    public ResponseEntity<Void> sendWelcomeEmail(Authentication connectedUser) {
        Jwt jwt = (Jwt) connectedUser.getPrincipal();
        authenticationService.sendWelcomeEmail(jwt);
        return ResponseEntity.ok().build();
    }
}