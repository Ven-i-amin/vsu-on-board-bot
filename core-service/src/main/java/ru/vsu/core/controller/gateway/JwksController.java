package ru.vsu.core.controller.gateway;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.core.service.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/gateway")
@AllArgsConstructor
public class JwksController {
    private JwtService jwtService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return jwtService.getJwks();
    }
}
