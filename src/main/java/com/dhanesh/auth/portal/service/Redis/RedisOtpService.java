package com.dhanesh.auth.portal.service.Redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.model.OtpData;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling OTP data storage and retrieval in Redis.
 * <p>
 * Stores OTPs with a TTL, verifies presence, and handles cleanup
 * after verification or expiration.
 */
@Service
@RequiredArgsConstructor
public class RedisOtpService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.otp.duration}")
    private long otpDuration;

    /**
     * Stores OTP data in Redis for a given email.
     * The data will expire automatically after the configured duration.
     *
     * @param email   the user's email used as Redis key
     * @param otpData the OTP data to store (code, purpose, timestamp, etc.)
     */
    public void saveOtpData(String email, OtpData otpData) {
        redisTemplate.opsForValue().set(email, otpData, otpDuration, TimeUnit.MINUTES);
    }

    /**
     * Retrieves the OTP data associated with the given email.
     *
     * @param email the email used as key
     * @return the stored OTP data, or null if not found or expired
     */
    public OtpData getOtpData(String email) {
        return (OtpData) redisTemplate.opsForValue().get(email);
    }

    /**
     * Checks if OTP data exists in Redis for the given email.
     *
     * @param email the email to check
     * @return true if OTP data exists, false if expired or never created
     */
    public boolean hasKey(String email) {
        return redisTemplate.hasKey(email);
    }

    /**
     * Deletes the OTP data associated with the given email from Redis.
     * Typically called after successful verification or on cleanup.
     *
     * @param email the email whose OTP data should be removed
     */
    public void deleteOtpData(String email) {
        redisTemplate.delete(email);
    }
}
