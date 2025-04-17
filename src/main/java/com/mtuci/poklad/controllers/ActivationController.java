package com.mtuci.poklad.controllers;

import com.mtuci.poklad.configuration.JwtTokenProvider;
import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.models.Ticket;
import com.mtuci.poklad.service.impl.DeviceServiceImpl;
import com.mtuci.poklad.service.impl.LicenseServiceImpl;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    /**
     * Метод активации лицензии.
     *
     * @param authHeader заголовок авторизации
     * @param name       имя устройства
     * @param macAddress MAC-адрес устройства
     * @param activationCode код активации лицензии
     * @return Ответ с тикетом активации
     */
    @PostMapping
    public ResponseEntity<?> activateLicense(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam String name,
                                             @RequestParam String macAddress,
                                             @RequestParam String activationCode) {
        try {

            String token = extractToken(authHeader);
            String login = jwtTokenProvider.getUsername(token);


            ApplicationUser user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));


            Device device = deviceService.registerOrUpdateDevice(name, macAddress, user);


            Ticket ticket = licenseService.activateLicense(activationCode, device, user);

            return ResponseEntity.ok(ticket);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный заголовок Authorization");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Ошибка: %s", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при активации лицензии: " + e.getMessage());
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
