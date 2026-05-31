package com.bsi.manajement_magang.shared;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2Hasher {
    private final Argon2PasswordEncoder encoder;

    public Argon2Hasher() {
        // Gunakan parameter bawaan default yang aman untuk Spring Security
        this.encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
