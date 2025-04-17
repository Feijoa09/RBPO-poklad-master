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
     * @param ownerId             ID владельца
     * @param deviceCount         Количество устройств
     * @return Ответ с созданной лицензией или ошибкой
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createLicense(
            @RequestParam("type_id") Long typeId,
            @RequestParam("product_id") Long productId,
            @RequestParam("owner_id") Long ownerId,
            @RequestParam("device_count") Integer deviceCount) {

        try {
            // Вызов метода для создания лицензии
            License license = licenseService.createLicense(
                    productId,
                    ownerId,
                    typeId,
                    deviceCount
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
                    license.getIsBlocked(),
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

