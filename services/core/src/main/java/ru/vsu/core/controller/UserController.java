package ru.vsu.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.vsu.core.model.dto.UserDto;
import ru.vsu.core.model.response.UserResponseDto;
import ru.vsu.core.service.UserService;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ResponseMapper responseMapper;

    @GetMapping("/{chatId}")
    public UserResponseDto getUser(@PathVariable Long chatId) {
        return responseMapper.toResponse(userService.findByChatId(chatId));
    }

    @PostMapping
    public UserResponseDto addUser(@RequestBody UserResponseDto user) {
        UserDto savedUser = userService.save(UserDto.builder()
                .chatId(user.chatId())
                .langCode(user.language())
                .build());
        return responseMapper.toResponse(savedUser);
    }
}
