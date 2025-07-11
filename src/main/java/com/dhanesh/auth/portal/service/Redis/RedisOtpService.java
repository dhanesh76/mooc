package com.dhanesh.auth.portal.service.Redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.model.OtpData;

import lombok.RequiredArgsConstructor;

/**
 * Redis service for managing OTP-related operations.
 */
@Service
@RequiredArgsConstructor
public class RedisOtpService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.otp.duration}")
    private long otpDuration;

    public void saveOtpData(String email, OtpData otpData) {
        redisTemplate.opsForValue().set(email, otpData, otpDuration, TimeUnit.MINUTES);
    }

    public OtpData getOtpData(String email) {
        return (OtpData) redisTemplate.opsForValue().get(email);
    }

    public boolean hasKey(String email) {
        return redisTemplate.hasKey(email);
    }

    public void deleteOtpData(String email) {
        redisTemplate.delete(email);
    }
}
