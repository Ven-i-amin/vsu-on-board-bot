package ru.vsu.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebAppController {
    @GetMapping({"/app", "/app/{path:[^.]+}", "/app/**/{path:[^.]+}"})
    public String forward() {
        return "forward:/app/index.html";
    }
}
