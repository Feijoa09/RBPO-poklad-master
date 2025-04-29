package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/signup")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserServiceImpl userService;

    /**
     * Регистрация нового пользователя.
     *
     * @param login    логин пользователя
     * @param email    email пользователя
     * @param password пароль пользователя
     * @return ответ с результатом регистрации
     */
    @PostMapping
    public ResponseEntity<?> registration(
            @RequestParam String login,
            @RequestParam String email,
            @RequestParam String password) {
        try {
            // Создаем нового пользователя
            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setLogin(login);
            applicationUser.setEmail(email);

            // Проверяем, можно ли сохранить пользователя (если пользователь уже существует)
            boolean isSaved = userService.saveUser(applicationUser, password);

            if (!isSaved) {
                return ResponseEntity.badRequest().body("Пользователь с таким логином или email уже существует!");
            }

            // Возвращаем успешный ответ с сообщением о регистрации
            return ResponseEntity.ok("Регистрация прошла успешно!");
        } catch (Exception e) {
            // Обработка исключений и возврат ошибки
            return ResponseEntity.status(500).body("Ошибка при регистрации: " + e.getMessage());
        }
    }
    @GetMapping
    public void test() {}
}
