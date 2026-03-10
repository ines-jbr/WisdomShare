package com.wisdomshare.model;

public record AuthenticationRequest(
    String email,
    String password
) {
}