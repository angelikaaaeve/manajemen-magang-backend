package com.bsi.manajement_magang.modules.iam;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks failed mentor-registration secret key attempts per client IP.
 * After 3 failed attempts, the IP is blocked from registering as mentor for 3 days.
 */
@Component
public class MentorRegistrationGuard {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofDays(3);

    private final ConcurrentHashMap<String, Attempt> attemptsByIp = new ConcurrentHashMap<>();

    public void assertNotBlocked(String clientIp) {
        Attempt attempt = attemptsByIp.get(clientIp);
        if (attempt != null && attempt.blockedUntil != null && Instant.now().isBefore(attempt.blockedUntil)) {
            throw new IllegalStateException(
                    "Terlalu banyak percobaan gagal. IP Anda diblokir untuk registrasi mentor hingga " + attempt.blockedUntil);
        }
    }

    public void recordFailure(String clientIp) {
        attemptsByIp.compute(clientIp, (ip, attempt) -> {
            Attempt current = attempt != null ? attempt : new Attempt();
            current.failedAttempts++;
            if (current.failedAttempts >= MAX_ATTEMPTS) {
                current.blockedUntil = Instant.now().plus(BLOCK_DURATION);
            }
            return current;
        });
    }

    public void recordSuccess(String clientIp) {
        attemptsByIp.remove(clientIp);
    }

    private static final class Attempt {
        private int failedAttempts = 0;
        private Instant blockedUntil = null;
    }
}
