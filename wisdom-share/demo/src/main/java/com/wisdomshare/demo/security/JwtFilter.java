package com.wisdomshare.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component

public class JwtFilter extends OncePerRequestFilter {

    // On a supprimé les "final" et le "UserDetailsService" pour casser la dépendance à la BDD

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Mode Bypass : On laisse tout passer directement vers Swagger/API
        // sans vérifier le JWT ni appeler la base de données.
        filterChain.doFilter(request, response);
    }
}