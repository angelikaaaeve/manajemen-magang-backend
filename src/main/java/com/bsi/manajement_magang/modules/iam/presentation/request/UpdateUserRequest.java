package com.bsi.manajement_magang.modules.iam.presentation.request;

public record UpdateUserRequest(
    String email,
    String nim,
    String nama,
    String noHp
) {}
