package com.oficiar.backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter extends GenericFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String header = httpRequest.getHeader("Authorization");

    System.out.println(">>> Authorization header: " + header);

    if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        boolean valid = jwtUtil.validateToken(token);
        System.out.println(">>> Token válido: " + valid);

        if (valid) {
            String email = jwtUtil.extractEmail(token);
            System.out.println(">>> Email extraído: " + email);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(email, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    chain.doFilter(request, response);
}
}