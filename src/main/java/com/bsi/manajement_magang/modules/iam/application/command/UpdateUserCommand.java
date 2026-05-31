package com.bsi.manajement_magang.modules.iam.application.command;

import java.util.UUID;

public record UpdateUserCommand(
    UUID userId,
    String email,
    String nim,
    String nama,
    String noHp
) {}
