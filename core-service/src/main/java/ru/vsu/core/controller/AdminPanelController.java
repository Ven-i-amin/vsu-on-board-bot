package ru.vsu.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPanelController {
    @GetMapping({"/admin", "/admin/{path:[^.]+}", "/admin/**/{path:[^.]+}"})
    public String forward() {
        return "forward:/admin/index.html";
    }
}
