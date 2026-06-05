package ru.vsu.core.service;

import java.util.Map;

public interface JwtService {
    String generateToken(String adminId);
    String extractAdminId(String token);
    Map<String, Object> getJwks();
}
