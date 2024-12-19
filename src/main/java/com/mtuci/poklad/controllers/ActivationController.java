package com.mtuci.poklad.controllers;

import com.mtuci.poklad.configuration.JwtTokenProvider;
import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.models.Ticket;
import com.mtuci.poklad.requests.DeviceRequest;
import com.mtuci.poklad.service.impl.DeviceServiceImpl;
import com.mtuci.poklad.service.impl.LicenseServiceImpl;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activation")
@RequiredArgsConstructor
public class ActivationController {

    private final UserServiceImpl userService;
    private final DeviceServiceImpl deviceService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LicenseServiceImpl licenseService;

    @PostMapping
    public ResponseEntity<?> activateLicense(@RequestHeader("Authorization") String authHeader, @RequestParam DeviceRequest deviceRequest) {
        try {
            // Извлечь токен и получить логин пользователя
            String token = extractToken(authHeader);
            String login = jwtTokenProvider.getUsername(token);

            // Получить пользователя из сервиса
            ApplicationUser user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Зарегистрировать или обновить устройство
            Device device = deviceService.registerOrUpdateDevice(deviceRequest.getName(), deviceRequest.getMacAddress(), user);

            // Активировать лицензию
            Ticket ticket = licenseService.activateLicense(deviceRequest.getActivationCode(), device, user);

            return ResponseEntity.ok(ticket);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(String.format("Ошибка: %s", e.getMessage()));
        }
    }

    /**
     * Извлечение токена из заголовка Authorization.
     *
     * @param authHeader Заголовок Authorization
     * @return Токен без префикса "Bearer "
     */
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Некорректный заголовок Authorization");
        }
        return authHeader.substring(7);
    }
}
