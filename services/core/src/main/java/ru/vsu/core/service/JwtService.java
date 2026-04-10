package ru.vsu.core.service;

public interface JwtService {
    String generateToken(String adminId);
    String extractAdminId(String token);
}
