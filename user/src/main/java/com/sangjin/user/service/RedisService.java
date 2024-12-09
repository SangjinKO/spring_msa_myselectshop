package com.sangjin.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangjin.user.dto.UserDto;
import com.sangjin.user.repository.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveUserCache(String userName, UserDto userDto)  {

        //JSON으로 처리 (TODO: 공통모듈 Object 분리)
        String json = null;
        try {
            json = objectMapper.writeValueAsString(userDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 값을 Redis에 저장하는 메서드
        redisTemplate.opsForValue().set(userName, json);
        redisTemplate.expire(userName, Duration.ofSeconds(60*60));
    }
}

