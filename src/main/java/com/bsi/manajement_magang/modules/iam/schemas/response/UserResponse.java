package com.bsi.manajement_magang.modules.iam.schemas.response;

import com.bsi.manajement_magang.enums.Gender;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String role,
    String nim,
    String nama,
    String noHp,
    Gender gender,
    String universitas
) {}
