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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
                req.noHp(),
                req.gender(),
                req.universitas()
        );
        return ResponseEntity.ok(iamService.register(command));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req, HttpServletResponse response) {
        LoginCommand command = new LoginCommand(req.email(), req.password());
        LoginResponse res = iamService.login(command);

        Cookie cookie = new Cookie("internflow_token", res.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Works on localhost and HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(3 * 24 * 60 * 60); // 3 days
        response.addCookie(cookie);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("internflow_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(iamService.getMe(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> update(@RequestBody UpdateUserRequest req) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        UUID userId = (UUID) auth.getPrincipal();
        UpdateUserCommand command = new UpdateUserCommand(
                userId,
                req.email(),
                req.nim(),
                req.nama(),
                req.noHp(),
                req.gender(),
                req.universitas()
        );
        return ResponseEntity.ok(iamService.update(command));
    }
}
