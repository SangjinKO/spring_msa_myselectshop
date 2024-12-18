package com.sangjin.product.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomPreAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.info("Products PreAuthFilter: request: " + request.getHeader("X-User-Name"));
        logger.info("Products PreAuthFilter: request: " + request.getHeader("X-User-Roles"));
        logger.info("Products PreAuthFilter: request: " + request.getHeader("X-User-Id"));

        // 헤더에서 사용자 정보와 역할(Role)을 추출
        String username = request.getHeader("X-User-Name");
        String rolesHeader = request.getHeader("X-User-Role");
        String userIdHeader = request.getHeader("X-User-Id");

        // userId를 Long으로 변환
        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid user ID format in header: " + userIdHeader);
        }

        if (username != null && rolesHeader != null) {
            // rolesHeader에 저장된 역할을 SimpleGrantedAuthority로 변환
            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesHeader.split(","))
                    .map(role -> new SimpleGrantedAuthority(role.trim()))
                    .collect(Collectors.toList());

            // 사용자 정보를 기반으로 인증 토큰 생성 --> userId 추가
            CustomUserPrincipal customPrincipal = new CustomUserPrincipal(username, null, userId, authorities);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(customPrincipal, null, authorities);


            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("Products PreAuthFilter: customPrincipal: " + "CREATED!" );
        } else {
            // 사용자 정보가 없을 경우 SecurityContext를 초기화
            SecurityContextHolder.clearContext();
            logger.info("Products PreAuthFilter: customPrincipal: " + "FAILED!" );
        }


        // 필터 체인을 계속해서 진행
        filterChain.doFilter(request, response);
    }
}
