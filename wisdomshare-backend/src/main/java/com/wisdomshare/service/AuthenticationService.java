package com.wisdomshare.service;

import com.wisdomshare.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EmailService emailService;

    /**
     * Called after Keycloak authenticates a user for the first time,
     * if you want to send a welcome email.
     * Pass the JWT from the Security context.
     */
    public void sendWelcomeEmail(Jwt jwt) {
        User user = User.fromJwt(jwt);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName() + ", welcome to WisdomShare",
                "Your account is now active. Enjoy WisdomShare!"
        );
    }
}