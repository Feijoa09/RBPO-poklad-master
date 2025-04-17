package com.mtuci.poklad.controllers;

import com.mtuci.poklad.configuration.JwtTokenProvider;
import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.Ticket;
import com.mtuci.poklad.service.impl.LicenseServiceImpl;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/license/renew")
@RequiredArgsConstructor
public class LicenseUpdateController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceImpl userService;
    private final LicenseServiceImpl licenseService;

    /**
     * Обновление лицензии пользователя.
     *
     * @param auth                заголовок авторизации
     * @param codeActivation      код активации
     * @return ответ с информацией о лицензиях или ошибкой
     */
    @PostMapping
    public ResponseEntity<?> licenseUpdate(@RequestHeader("Authorization") String auth, @RequestParam String codeActivation) {
        try {
            // Извлечение логина из токена
            String login = jwtTokenProvider.getUsername(auth.split(" ")[1]);

            // Получение пользователя по логину
            ApplicationUser user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Обработка продления лицензии
            List<Ticket> tickets = licenseService.licenseRenewal(codeActivation, user);

            // Возвращаем обновленные билеты
            return ResponseEntity.ok(tickets);
        } catch (RuntimeException e) {
            // Возвращаем ошибку с сообщением
            return ResponseEntity.badRequest().body(String.format("Ошибка: %s", e.getMessage()));
        }
    }
}
