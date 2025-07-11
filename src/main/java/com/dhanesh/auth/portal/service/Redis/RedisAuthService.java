package com.dhanesh.auth.portal.service.Redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.auth.SignupTempData;

import lombok.RequiredArgsConstructor;

/**
 * Redis service to manage temporary registration data.
 */
@Service
@RequiredArgsConstructor
public class RedisAuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> userNameCache;

    @Value("${app.session.duration}")
    private long sessionTTL;

    public void storeSignupData(String email, SignupTempData data) {
        userNameCache.opsForValue().set(data.username(), true, sessionTTL, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("signup:" + email, data, sessionTTL, TimeUnit.MINUTES);
    }

    public Optional<SignupTempData> getSignupData(String email) {
        Object value = redisTemplate.opsForValue().get("signup:" + email);
        return Optional.ofNullable((SignupTempData) value);
    }

    public boolean existsByEmail(String email) {
        return redisTemplate.hasKey("signup:" + email);
    }

    public boolean existsByUsername(String username) {
        return userNameCache.hasKey(username);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void deleteSignupData(String email, String userName) {
        redisTemplate.delete("signup:" + email);
        userNameCache.delete(userName);
    }
}
