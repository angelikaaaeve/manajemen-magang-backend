package com.bsi.manajement_magang.modules.iam.schemas.request;

import com.bsi.manajement_magang.enums.Gender;

public record UpdateUserRequest(
    String email,
    String nim,
    String nama,
    String noHp,
    Gender gender,
    String universitas
) {}
