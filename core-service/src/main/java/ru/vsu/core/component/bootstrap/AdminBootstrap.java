package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.entity.Admin;
import ru.vsu.core.repository.mongo.AdminRepository;

@Component
@Order(5)
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap.email:admin}")
    private String adminEmail;

    @Value("${app.admin.bootstrap.password:admin}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (adminRepository.count() > 0) {
            return;
        }

        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        adminRepository.save(Admin.builder()
                .email(adminEmail.trim())
                .passwordHash(passwordEncoder.encode(adminPassword))
                .build());
    }
}
