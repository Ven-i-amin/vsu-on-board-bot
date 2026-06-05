package ru.vsu.core.service.user;

public interface AdminService {
    String register(String email, String password);
    String login(String email, String password);
}
