package com.bsi.manajement_magang.modules.iam.application.response;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String role,
    String nim,
    String nama,
    String noHp
) {}
