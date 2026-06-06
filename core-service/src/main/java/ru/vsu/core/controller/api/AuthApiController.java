package ru.vsu.core.controller.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vsu.core.model.request.AdminLoginRequest;
import ru.vsu.core.model.request.AdminRegisterRequest;
import ru.vsu.core.model.response.AuthTokenResponse;
import ru.vsu.core.service.user.AdminService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthApiController {
    private final AdminService adminService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthTokenResponse register(@RequestBody @Valid AdminRegisterRequest request) {
        return new AuthTokenResponse(adminService.register(request.email(), request.password()));
    }

    @PostMapping("/login")
    public AuthTokenResponse login(@RequestBody @Valid AdminLoginRequest request) {
        return new AuthTokenResponse(adminService.login(request.email(), request.password()));
    }
}
