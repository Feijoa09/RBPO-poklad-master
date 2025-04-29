package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.TokensUser;
import com.mtuci.poklad.repositories.UserRepository;
import com.mtuci.poklad.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import com.mtuci.poklad.models.ApplicationUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Метод для аутентификации пользователя и создания пары токенов (accessToken и refreshToken).
     *
     * @param login    логин пользователя
     * @param device_id device_id пользователя
     * @return Ответ с токенами и логином пользователя, если аутентификация успешна
     */

    @PostMapping("/signin")
    public ResponseEntity<?> login(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String device_id) {
        System.out.println("login exist");
        try {
            // Аутентификация по логину и паролю
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );

            // Поиск пользователя
            ApplicationUser user = userRepository.findByLogin(login)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Создание токенов
            TokensUser tokensUser = tokenService.createSession(login, device_id);

            return ResponseEntity.ok(tokensUser);

        } catch (ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body("Оптимистичная блокировка обнаружена! Пожалуйста, попробуйте позже.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        }
    }


    /**
     * Метод для обновления токенов с использованием refreshToken.
     *
     * @param refreshToken Токен обновления
     * @param deviceId     Идентификатор устройства
     * @return Ответ с новой парой токенов, если обновление успешно
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokens(
            @RequestParam String refreshToken,
            @RequestParam String deviceId) {

        try {
            // Обновляем сессию, генерируем новые токены
            TokensUser tokensUser = tokenService.updateSession(refreshToken, deviceId);

            // Возвращаем обновленные токены
            return ResponseEntity.ok(tokensUser);

        } catch (ObjectOptimisticLockingFailureException e) {
            // Обрабатываем исключение оптимистичной блокировки
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body("Оптимистичная блокировка обнаружена! Пожалуйста, попробуйте позже.");
        } catch (Exception e) {
            // Обрабатываем ошибки типа неверный refreshToken
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token or session not found");
        }
    }
}
