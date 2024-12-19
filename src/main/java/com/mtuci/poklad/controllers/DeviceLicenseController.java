package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.DeviceLicense;
import com.mtuci.poklad.requests.DataDeviceLicenseRequest;
import com.mtuci.poklad.service.impl.DeviceLicenseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manage/deviceLicense")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DeviceLicenseController {

    private final DeviceLicenseServiceImpl deviceLicenseService;

    /**
     * Сохранение новой связи устройства и лицензии.
     *
     * @param request данные для создания связи устройства и лицензии
     * @return ответ с данными созданной связи
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam DataDeviceLicenseRequest request) {
        try {
            // Сохраняем связь через сервис
            DeviceLicense deviceLicense = deviceLicenseService.save(
                    request.getDeviceId(),
                    request.getLicenseId(),
                    request.getActivationDate()
            );

            // Устанавливаем ID и возвращаем ответ
            request.setId(deviceLicense.getId());
            return ResponseEntity.status(201).body(request); // Статус 201 для успешного создания
        } catch (Exception e) {
            return handleError("Ошибка при создании связи устройства и лицензии", e);
        }
    }

    /**
     * Получение всех связей устройств и лицензий.
     *
     * @return список всех связей
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<DeviceLicense> deviceLicenses = deviceLicenseService.getAll();
            List<DataDeviceLicenseRequest> dataDevices = deviceLicenses.stream()
                    .map(deviceLicense -> new DataDeviceLicenseRequest(
                            deviceLicense.getId(),
                            deviceLicense.getDevice().getId(),
                            deviceLicense.getLicense().getId(),
                            deviceLicense.getActivationDate()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dataDevices); // Статус 200 для успешного запроса
        } catch (Exception e) {
            return handleError("Ошибка при получении списка связей устройства и лицензии", e);
        }
    }

    /**
     * Обновление связи устройства и лицензии.
     *
     * @param request новые данные для обновления связи
     * @return ответ с обновленными данными связи
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam DataDeviceLicenseRequest request) {
        try {
            // Обновляем связь через сервис
            DeviceLicense updatedDeviceLicense = deviceLicenseService.update(
                    request.getId(),
                    request.getDeviceId(),
                    request.getLicenseId(),
                    request.getActivationDate()
            );

            // Возвращаем обновленные данные
            return ResponseEntity.ok(request); // Статус 200 для успешного обновления
        } catch (Exception e) {
            return handleError("Ошибка при обновлении связи устройства и лицензии", e);
        }
    }

    /**
     * Удаление связи устройства и лицензии.
     *
     * @param id идентификатор связи для удаления
     * @return ответ с сообщением о успешном удалении
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            deviceLicenseService.delete(id);
            return ResponseEntity.ok("Связь устройства и лицензии удалена"); // Статус 200 для успешного удаления
        } catch (Exception e) {
            return handleError("Ошибка при удалении связи устройства и лицензии", e);
        }
    }

    /**
     * Универсальный метод для обработки ошибок.
     *
     * @param message сообщение об ошибке
     * @param e       исключение
     * @return ответ с сообщением об ошибке
     */
    private ResponseEntity<?> handleError(String message, Exception e) {
        return ResponseEntity.status(400).body(message + ": " + e.getMessage()); // Статус 400 для ошибки
    }
}

