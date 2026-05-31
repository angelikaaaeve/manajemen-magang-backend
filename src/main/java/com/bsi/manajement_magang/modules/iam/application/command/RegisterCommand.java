package com.bsi.manajement_magang.modules.iam.application.command;

import com.bsi.manajement_magang.modules.iam.domain.Role;

public record RegisterCommand(
    String email,
    String password,
    Role role,
    String nim,
    String nama,
    String noHp
) {}
