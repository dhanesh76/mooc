package com.dhanesh.auth.portal.service.Redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.auth.SignupTempData;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing temporary signup session data in Redis.
 * <p>
 * Stores user registration data with TTL, enforces uniqueness on email and username,
 * and cleans up stale session data after successful verification.
 */
@Service
@RequiredArgsConstructor
public class RedisAuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> userNameCache;

    @Value("${app.session.duration}")
    private long sessionTTL;

    /**
     * Stores temporary signup data in Redis, associated with the given email.
     * Also caches the username to prevent duplicate registrations during session TTL.
     *
     * @param email the email used for signup
     * @param data  the temporary signup data (username, encoded password, etc.)
     */
    public void storeSignupData(String email, SignupTempData data) {
        userNameCache.opsForValue().set(data.username(), true, sessionTTL, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("signup:" + email, data, sessionTTL, TimeUnit.MINUTES);
    }

    /**
     * Retrieves signup data associated with a given email, if it exists in Redis.
     *
     * @param email the email used for signup
     * @return an Optional containing SignupTempData if present, or empty if expired/not found
     */
    public Optional<SignupTempData> getSignupData(String email) {
        Object value = redisTemplate.opsForValue().get("signup:" + email);
        return Optional.ofNullable((SignupTempData) value);
    }

    /**
     * Checks if Redis contains a signup session for the given email.
     *
     * @param email the email to check
     * @return true if signup data exists for the email, false otherwise
     */
    public boolean existsByEmail(String email) {
        return redisTemplate.hasKey("signup:" + email);
    }

    /**
     * Checks if a username is already stored in Redis for uniqueness validation.
     *
     * @param username the username to check
     * @return true if the username exists in cache, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userNameCache.hasKey(username);
    }

    /**
     * Checks for the existence of a generic Redis key.
     *
     * @param key the Redis key to check
     * @return true if the key exists, false otherwise
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * Removes both the signup session associated with the given email
     * and the username cache entry from Redis.
     *
     * @param email    the email associated with the signup session
     * @param userName the username to remove from uniqueness cache
     */
    public void deleteSignupData(String email, String userName) {
        redisTemplate.delete("signup:" + email);
        userNameCache.delete(userName);
    }
}
