package com.bsi.manajement_magang.modules.iam.application.response;

public record LoginResponse(
    String token,
    String email,
    String role
) {}
