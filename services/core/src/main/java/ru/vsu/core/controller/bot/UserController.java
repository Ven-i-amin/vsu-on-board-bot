package ru.vsu.core.controller.bot;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.core.component.mapper.ResponseMapper;
import ru.vsu.core.model.dto.UserDto;
import ru.vsu.core.service.UserService;

@RestController
@RequestMapping("/bot/user")
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
                .langCode(user.langCode())
                .build());
        return responseMapper.toResponse(savedUser);
    }

    @PutMapping("/{chatId}/langCode")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateLangCode(@PathVariable Long chatId, @RequestBody UserResponseDto user) {
        UserDto savedUser = userService.updateLangCode(chatId, user.langCode());
        return responseMapper.toResponse(savedUser);
    }
}
