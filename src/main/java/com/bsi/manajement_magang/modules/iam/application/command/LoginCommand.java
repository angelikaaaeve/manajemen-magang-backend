package com.bsi.manajement_magang.modules.iam.application.command;

public record LoginCommand(
    String email,
    String password
) {}
