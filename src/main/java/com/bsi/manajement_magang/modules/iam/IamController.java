package com.bsi.manajement_magang.modules.iam;

import com.bsi.manajement_magang.modules.iam.schemas.request.LoginRequest;
import com.bsi.manajement_magang.modules.iam.schemas.request.RegisterRequest;
import com.bsi.manajement_magang.modules.iam.schemas.request.UpdateUserRequest;
import com.bsi.manajement_magang.modules.iam.schemas.response.LoginResponse;
import com.bsi.manajement_magang.modules.iam.schemas.response.UserResponse;
import com.bsi.manajement_magang.modules.iam.IamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest req, HttpServletRequest request) {
        return ResponseEntity.ok(iamService.register(req, resolveClientIp(request)));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req, HttpServletResponse response) {
        LoginResponse res = iamService.login(req);

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
        return ResponseEntity.ok(iamService.update(userId, req));
    }
}
