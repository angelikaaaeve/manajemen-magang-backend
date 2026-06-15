package com.bsi.manajement_magang.modules.iam.schemas.request;

public record UpdateUserRequest(
    String email,
    String nim,
    String nama,
    String noHp,
    String gender,
    String universitas
) {}
