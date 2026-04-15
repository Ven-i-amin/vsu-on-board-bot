package ru.vsu.core.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.core.model.entity.Admin;
import ru.vsu.core.repository.AdminRepository;
import ru.vsu.core.service.AdminService;
import ru.vsu.core.service.JwtService;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(String email, String password) {
        if (adminRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin with this email already exists");
        }

        Admin admin = adminRepository.save(Admin.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .build());

        return jwtService.generateToken(admin.getAdminId());
    }

    @Override
    public String login(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return jwtService.generateToken(admin.getAdminId());
    }
}
