package com.sangjin.config;

import com.sangjin.dto.UserDto;
import com.sangjin.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Optional;

@Slf4j(topic = "GatewaySecurity")
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final RedisService redisService;
    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKeyString;

    public SecurityConfig(RedisService redisService) {
        this.redisService = redisService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 비활성화
//                .addFilterAt(jwtAuthenticationFilter(redisService), SecurityWebFiltersOrder.HTTP_BASIC);
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC);

        return http.build();
    }

//    public WebFilter jwtAuthenticationFilter(RedisService redisService) {
    public WebFilter jwtAuthenticationFilter() {

        return (exchange, chain) -> {

            // /auth/login 경로는 필터를 적용하지 않음
            if (exchange.getRequest().getURI().getPath().equals("/api/auth/signup")
                    || exchange.getRequest().getURI().getPath().equals("/api/auth/login")) {
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    byte[] bytes = Base64.getDecoder().decode(secretKeyString);
                    var secretKey = Keys.hmacShaKeyFor(bytes);

                    Claims claims = Jwts
                            .parserBuilder()
                            .setSigningKey(secretKey).build()
                            .parseClaimsJws(token)
                            .getBody();

                    String username = claims.getSubject();
                    String userrole = claims.get("auth", String.class);

                    // 캐싱된 로그인 유저정보(DB) 확인
                    var userDto =
                            Optional.ofNullable(
                                            redisService.getCachedUser(username)
                                    )
                                    .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found")
                                    );


                    log.info("JwtAuthenticationFilter: Redis: UserDto" + userDto.getId().toString() + userDto.getUsername() + userDto.getRole().toString());
                    if (!userDto.getRole().toString().equals(userrole)) {
                        throw new UsernameNotFoundException("User " + username + " does NOT have the role of " + userrole);
                    }

                    // 사용자 정보를 새로운 헤더에 추가
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Name", username)  // 사용자명 헤더 추가
                           .header("X-User-Role", userrole)    // 권한 정보 헤더 추가, 중복권한
                            .header("X-User-Id", userDto.getId().toString()) // 레디스에서 조회하여 설정
                            .build();

                    // 수정된 요청으로 필터 체인 계속 처리
                    ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
                    return chain.filter(modifiedExchange);

                    // 추가적인 JWT 처리 로직을 넣을 수 있음
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("Invalid JWT Token"));
                }
            }

            return chain.filter(exchange);
        };
    }
}
