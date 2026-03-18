package com.analiso.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;

    /** Quando true (apenas em dev), requisições sem token recebem um usuário fixo. */
    @Value("${dev.auth.bypass.enabled:false}")
    private boolean bypassEnabled;

    /** ID do usuário injetado automaticamente quando o bypass está ativo. */
    @Value("${dev.auth.bypass.user-id:1}")
    private long bypassUserId;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length()).trim();
            Optional<Long> userId = jwtTokenService.extractUserId(token);

            if (userId.isPresent()) {
                setAuthentication(request, userId.get());
            }
        } else if (bypassEnabled) {
            // Dev bypass: autentica automaticamente sem token.
            // NUNCA ativo em produção (dev.auth.bypass.enabled não está em application.properties).
            setAuthentication(request, bypassUserId);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, long userId) {
        AuthenticatedUser principal = new AuthenticatedUser(userId);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, List.of());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
