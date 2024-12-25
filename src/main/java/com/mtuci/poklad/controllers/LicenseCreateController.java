package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.License;
import com.mtuci.poklad.requests.DataLicenseRequest;
import com.mtuci.poklad.service.impl.LicenseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/license/add")
@RequiredArgsConstructor
public class LicenseCreateController {

    private final LicenseServiceImpl licenseService;

    /**
     * Создание новой лицензии.
     *
     * @param typeId              ID типа лицензии
     * @param productId           ID продукта
     * @param userId              ID пользователя
     * @param ownerId             ID владельца
     * @param firstActivationDate Дата активации
     * @param endingDate          Дата окончания
     * @param blocked             Статус блокировки
     * @param deviceCount         Количество устройств
     * @param duration            Продолжительность лицензии
     * @param code                Код лицензии
     * @param description         Описание лицензии
     * @return Ответ с созданной лицензией или ошибкой
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createLicense(
            @RequestParam("type_id") Long typeId,
            @RequestParam("product_id") Long productId,
            @RequestParam("user_id") Long userId,
            @RequestParam("owner_id") Long ownerId,
            @RequestParam("first_activation_date") String firstActivationDate,
            @RequestParam("ending_date") String endingDate,
            @RequestParam("blocked") boolean blocked,
            @RequestParam("device_count") Integer deviceCount,
            @RequestParam("duration") Long duration,
            @RequestParam("code") String code,
            @RequestParam("description") String description) {

        try {
            // Вызов метода для создания лицензии
            License license = licenseService.createLicense(
                    productId,
                    ownerId,
                    typeId,
                    deviceCount,
                    duration,
                    firstActivationDate,
                    endingDate,
                    blocked,
                    code,
                    description
            );

            // Формируем ответ с данными лицензии
            DataLicenseRequest dataLicenseRequest = new DataLicenseRequest(
                    license.getId(), // id теперь будет автоматически присвоено
                    license.getLicenseType().getId(),
                    license.getProduct().getId(),
                    license.getUser() != null ? license.getUser().getId() : null,  // Если нужно, добавьте ID пользователя, если он есть
                    license.getOwner().getId(),
                    license.getFirstActivationDate(),
                    license.getEndingDate(),
                    license.isBlocked(),
                    license.getDeviceCount(),
                    license.getDuration(),
                    license.getCode(),
                    license.getDescription()
            );

            // Возвращаем успешный ответ
            return ResponseEntity.ok(dataLicenseRequest);

        } catch (NumberFormatException e) {
            // Обработка ошибки преобразования типов
            return ResponseEntity.badRequest().body("Ошибка в формате числового значения: " + e.getMessage());
        } catch (RuntimeException e) {
            // Общая обработка ошибок
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
}

