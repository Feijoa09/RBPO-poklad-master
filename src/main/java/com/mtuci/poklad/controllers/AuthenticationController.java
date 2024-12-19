package com.mtuci.poklad.controllers;

import com.mtuci.poklad.configuration.JwtTokenProvider;
import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.AuthenticationResponse;
import com.mtuci.poklad.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Метод для аутентификации пользователя.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return Ответ с токеном и логином пользователя, если аутентификация успешна
     */
    @PostMapping
    public ResponseEntity<?> login(
            @RequestParam String login,
            @RequestParam String password) {
        try {
            // Аутентификация пользователя с использованием AuthenticationManager
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );

            // Получаем пользователя из базы данных
            ApplicationUser user = userRepository.findByLogin(login)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Генерируем токен для пользователя
            String token = jwtTokenProvider.createToken(login, user.getRole().getGrantedAuthorities());

            // Возвращаем токен и логин пользователя
            return ResponseEntity.ok(new AuthenticationResponse(token, login));

        } catch (AuthenticationException e) {
            // Обрабатываем ошибку аутентификации
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        }
    }
}
