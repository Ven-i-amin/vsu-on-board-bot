package ru.vsu.core.controller.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.core.model.request.AdminLoginRequest;
import ru.vsu.core.model.request.AdminRegisterRequest;
import ru.vsu.core.model.response.AuthTokenResponse;
import ru.vsu.core.service.AdminService;
import ru.vsu.core.service.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class AuthApiController {
    private final AdminService adminService;

    @PostMapping("/register")
    public AuthTokenResponse register(@RequestBody @Valid AdminRegisterRequest request) {
        return new AuthTokenResponse(adminService.register(request.email(), request.password()));
    }

    @PostMapping("/login")
    public AuthTokenResponse login(@RequestBody @Valid AdminLoginRequest request) {
        return new AuthTokenResponse(adminService.login(request.email(), request.password()));
    }
}
