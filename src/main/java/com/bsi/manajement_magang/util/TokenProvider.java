package com.bsi.manajement_magang.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class TokenProvider {
    private static final String SECRET = "skripsi_manajemen_magang_2026_super_secret_key_extremely_long_and_secure";
    private static final long EXPIRY_MS = 259200000L; // 3 hari

    public static record Claims(UUID userId, String role) {}

    public static String generateToken(UUID userId, String role) {
        long expiry = System.currentTimeMillis() + EXPIRY_MS;
        String payload = userId.toString() + ":" + role + ":" + expiry;
        String signature = hmac(payload);
        String rawToken = payload + ":" + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
    }

    public static Claims validateToken(String token) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
            String rawToken = new String(decodedBytes, StandardCharsets.UTF_8);
            String[] parts = rawToken.split(":");
            if (parts.length != 4) {
                return null;
            }
            String userIdStr = parts[0];
            String role = parts[1];
            long expiry = Long.parseLong(parts[2]);
            String signature = parts[3];

            String payload = userIdStr + ":" + role + ":" + expiry;
            String expectedSignature = hmac(payload);

            if (!expectedSignature.equals(signature)) {
                return null; // Tanda tangan tidak valid
            }

            if (System.currentTimeMillis() > expiry) {
                return null; // Token kedaluwarsa
            }

            return new Claims(UUID.fromString(userIdStr), role);
        } catch (Exception e) {
            return null;
        }
    }

    private static String hmac(String data) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
