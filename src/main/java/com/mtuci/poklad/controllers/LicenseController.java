package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.License;
import com.mtuci.poklad.requests.DataLicenseRequest;
import com.mtuci.poklad.service.impl.LicenseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settings/license")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LicenseController {
    private final LicenseServiceImpl licenseService;


    @PostMapping
    public ResponseEntity<?> save(
            @RequestParam("id") Long id,
            @RequestParam("licenseTypeId") Long licenseTypeId,
            @RequestParam("productId") Long productId,
            @RequestParam(required = false, value = "userId") Long userId,
            @RequestParam("ownerId") Long ownerId,
            @RequestParam("firstActivationDate") String firstActivationDate,
            @RequestParam("endingDate") String endingDate,
            @RequestParam("isBlocked") boolean isBlocked,
            @RequestParam("deviceCount") int deviceCount,
            @RequestParam("duration") Long duration,
            @RequestParam("code") String code,
            @RequestParam("description") String description) {
        try {
            // Преобразование строк в даты
            java.sql.Date firstDate = java.sql.Date.valueOf(firstActivationDate);
            java.sql.Date endDate = java.sql.Date.valueOf(endingDate);

            // Создаем объект запроса для сохранения
            DataLicenseRequest request = new DataLicenseRequest(
                    id, licenseTypeId, productId, userId, ownerId,
                    firstDate, endDate, isBlocked, deviceCount, duration, code, description);

            // Сохраняем лицензию через сервис
            License license = licenseService.save(request);

            // Устанавливаем ID из сохраненной лицензии
            request.setId(license.getId());

            // Возвращаем ответ с сохраненной лицензией
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            // Возвращаем ошибку с подробным сообщением в случае исключения
            return ResponseEntity.badRequest().body("Ошибка при сохранении лицензии: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<License> licenses = licenseService.getAll();
            List<DataLicenseRequest> data = licenses.stream().map(
                    license -> new DataLicenseRequest(
                            license.getId(),
                            license.getLicenseType().getId(),
                            license.getProduct().getId(),
                            license.getUser() != null ? license.getUser().getId() : null,
                            license.getOwner().getId(),
                            license.getFirstActivationDate(),
                            license.getEndingDate(),
                            license.isBlocked(),
                            license.getDeviceCount(),
                            license.getDuration(),
                            license.getCode(),
                            license.getDescription()
                    )
            ).toList();
            return ResponseEntity.ok(data);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Для обновления данных
    @PutMapping
    public ResponseEntity<?> update(
            @RequestParam("id") Long id,
            @RequestParam("licenseTypeId") Long licenseTypeId,
            @RequestParam("productId") Long productId,
            @RequestParam(required = false, value = "userId") Long userId,
            @RequestParam("ownerId") Long ownerId,
            @RequestParam("firstActivationDate") String firstActivationDate,
            @RequestParam("endingDate") String endingDate,
            @RequestParam("isBlocked") boolean isBlocked,
            @RequestParam("deviceCount") int deviceCount,
            @RequestParam("duration") Long duration,
            @RequestParam("code") String code,
            @RequestParam("description") String description) {
        try {
            // Валидация даты на корректность
            java.sql.Date firstDate;
            java.sql.Date endDate;

            try {
                firstDate = java.sql.Date.valueOf(firstActivationDate);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Некорректная дата активации (firstActivationDate). Используйте формат YYYY-MM-DD.");
            }

            try {
                endDate = java.sql.Date.valueOf(endingDate);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Некорректная дата окончания (endingDate). Используйте формат YYYY-MM-DD.");
            }

            // Проверка обязательных полей на наличие значений
            if (code == null || code.isEmpty() || description == null || description.isEmpty()) {
                return ResponseEntity.badRequest().body("Поле 'code' или 'description' не может быть пустым.");
            }

            // Создаем объект запроса для обновления
            DataLicenseRequest request = new DataLicenseRequest(
                    id, licenseTypeId, productId, userId, ownerId,
                    firstDate, endDate, isBlocked, deviceCount, duration, code, description);

            // Обновляем лицензию через сервис
            licenseService.update(request);

            // Возвращаем успешный ответ с обновленными данными запроса
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            // Логируем ошибку для диагностики
            e.printStackTrace();

            // Возвращаем ошибку с подробным сообщением
            return ResponseEntity.badRequest().body("Ошибка при обновлении лицензии: " + e.getMessage());
        }
    }



    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            licenseService.delete(id);
            return ResponseEntity.ok("Лицензия удалена");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
