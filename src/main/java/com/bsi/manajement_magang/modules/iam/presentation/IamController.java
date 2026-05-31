package com.bsi.manajement_magang.modules.iam.presentation;

import com.bsi.manajement_magang.modules.iam.application.IamService;
import com.bsi.manajement_magang.modules.iam.application.command.LoginCommand;
import com.bsi.manajement_magang.modules.iam.application.command.RegisterCommand;
import com.bsi.manajement_magang.modules.iam.application.command.UpdateUserCommand;
import com.bsi.manajement_magang.modules.iam.application.response.LoginResponse;
import com.bsi.manajement_magang.modules.iam.application.response.UserResponse;
import com.bsi.manajement_magang.modules.iam.domain.Role;
import com.bsi.manajement_magang.modules.iam.presentation.request.LoginRequest;
import com.bsi.manajement_magang.modules.iam.presentation.request.RegisterRequest;
import com.bsi.manajement_magang.modules.iam.presentation.request.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/iam")
public class IamController {
    private final IamService iamService;

    public IamController(IamService iamService) {
        this.iamService = iamService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest req) {
        RegisterCommand command = new RegisterCommand(
                req.email(),
                req.password(),
                Role.fromString(req.role()),
                req.nim(),
                req.nama(),
                req.noHp()
        );
        return ResponseEntity.ok(iamService.register(command));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        LoginCommand command = new LoginCommand(req.email(), req.password());
        return ResponseEntity.ok(iamService.login(command));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iamService.getMe(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> update(@RequestBody UpdateUserRequest req) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UpdateUserCommand command = new UpdateUserCommand(
                userId,
                req.email(),
                req.nim(),
                req.nama(),
                req.noHp()
        );
        return ResponseEntity.ok(iamService.update(command));
    }
}
