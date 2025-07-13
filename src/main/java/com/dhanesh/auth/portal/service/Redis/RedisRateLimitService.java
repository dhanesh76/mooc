package com.dhanesh.auth.portal.service.Redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class RedisRateLimitService {

    private final RedisTemplate<String, Object> template;

    @Value("${ip.otp.limit}")
    private long otpLimit;

    @Value("${ip.otp.limit.duration}")
    private long duration;

    @Value("${otp.cooldown}")
    private long cooldown;

    /**
     * Checks if the IP is under cooldown. If not, set a cooldown lock.
     */
    public boolean isInCooldown(String ip) {
        String cooldownKey = "authportal:cooldown:ip:" + ip;

        if (template.hasKey(cooldownKey)) {
            return true; // Still in cooldown
        }

        template.opsForValue().set(cooldownKey, "LOCKED", cooldown, TimeUnit.SECONDS);
        return false;
    }

    /**
     * Rate limits max OTP requests per X minutes.
     */
    public boolean isAllowed(String ip) {
        String rateKey = "authportal:rate:ip:" + ip;

        Long incrementResult = template.opsForValue().increment(rateKey);
        long count = (incrementResult != null) ? incrementResult : 0L;

        if (count == 1L) {
            template.expire(rateKey, duration, TimeUnit.MINUTES);
        }

        return count <= otpLimit;
    }

    public long getCooldownRemaining(String ip) {
        String cooldownKey = "authportal:cooldown:ip:" + ip;
        Long remaining = template.getExpire(cooldownKey, TimeUnit.SECONDS);
        return (remaining != null && remaining > 0) ? remaining : 0;
    }
}
