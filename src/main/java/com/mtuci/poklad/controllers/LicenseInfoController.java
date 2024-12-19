package com.mtuci.poklad.controllers;

import com.mtuci.poklad.configuration.JwtTokenProvider;
import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.models.License;
import com.mtuci.poklad.models.Ticket;
import com.mtuci.poklad.requests.DeviceInfoRequest;
import com.mtuci.poklad.service.impl.DeviceServiceImpl;
import com.mtuci.poklad.service.impl.LicenseServiceImpl;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/licenseInfo")
@RequiredArgsConstructor
public class LicenseInfoController {

    private final UserServiceImpl userService;
    private final DeviceServiceImpl deviceService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LicenseServiceImpl licenseService;

    /**
     * Получение информации о лицензиях для устройства.
     *
     * @param auth               заголовок авторизации
     * @param deviceInfoRequest запрос с информацией об устройстве
     * @return список билетов с информацией о лицензиях для устройства
     */
    @PostMapping
    public ResponseEntity<?> getLicenseInfo(@RequestHeader("Authorization") String auth, @RequestParam DeviceInfoRequest deviceInfoRequest) {
        try {
            // Извлечение логина из токена
            String login = jwtTokenProvider.getUsername(auth.split(" ")[1]);

            // Получение пользователя по логину
            ApplicationUser user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Поиск устройства по имени и MAC-адресу
            Device device = deviceService.findDeviceByInfo(deviceInfoRequest.getName(), deviceInfoRequest.getMacAddress(), user)
                    .orElseThrow(() -> new RuntimeException("Устройство не найдено"));

            // Получение активных лицензий для устройства
            List<License> licenses = licenseService.getActiveLicensesForDevice(device, user);

            // Генерация билетов для каждой лицензии
            List<Ticket> tickets = licenses.stream()
                    .map(license -> licenseService.generateTicket(license, device, "Информация о лицензии на текущее устройство"))
                    .toList();

            return ResponseEntity.ok(tickets);

        } catch (RuntimeException e) {
            // Обработка ошибок и возврат сообщения
            return ResponseEntity.badRequest().body(String.format("Ошибка(%s)", e.getMessage()));
        }
    }
}
