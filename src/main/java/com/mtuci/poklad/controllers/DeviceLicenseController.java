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
@RequestMapping("/settings/deviceLicense")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DeviceLicenseController {

    private final DeviceLicenseServiceImpl deviceLicenseService;

    /**
     * Сохранение новой связи устройства и лицензии.
     * @param id             идентификатор
     * @param deviceId       идентификатор устройства
     * @param licenseId      идентификатор лицензии
     * @param activationDate дата активации лицензии в формате YYYY-MM-DD
     * @return ответ с данными созданной связи
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam("id") Long id,
                                  @RequestParam("deviceId") Long deviceId,
                                  @RequestParam("licenseId") Long licenseId,
                                  @RequestParam("activationDate") String activationDate) {
        try {
            Date date = Date.valueOf(activationDate); // Преобразуем строку в дату

            // Создаём и сохраняем связь устройства и лицензии
            DeviceLicense deviceLicense = deviceLicenseService.save(id, deviceId, licenseId, date);

            // Возвращаем ответ с данными созданной связи
            return ResponseEntity.status(HttpStatus.CREATED).body(deviceLicense); // Статус 201 для успешного создания
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Некорректный формат даты. Используйте формат YYYY-MM-DD: " + e.getMessage());
        } catch (Exception e) {
            return handleError("Ошибка при создании связи устройства и лицензии", e);
        }
    }

    /**
     * Обновление существующей связи устройства и лицензии.
     *
     * @param id             идентификатор связи
     * @param deviceId       новый идентификатор устройства
     * @param licenseId      новый идентификатор лицензии
     * @param activationDate новая дата активации лицензии в формате YYYY-MM-DD
     * @return ответ с обновленными данными связи
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam("id") Long id,
                                    @RequestParam("deviceId") Long deviceId,
                                    @RequestParam("licenseId") Long licenseId,
                                    @RequestParam("activationDate") String activationDate) {
        try {
            Date date = Date.valueOf(activationDate); // Преобразование строки в дату

            // Обновляем связь через сервис
            DeviceLicense updatedDeviceLicense = deviceLicenseService.update(id, deviceId, licenseId, date);

            // Возвращаем ответ с обновленными данными связи
            return ResponseEntity.ok(updatedDeviceLicense); // Статус 200 для успешного обновления
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Некорректный формат даты. Используйте формат YYYY-MM-DD: " + e.getMessage());
        } catch (Exception e) {
            return handleError("Ошибка при обновлении связи устройства и лицензии", e);
        }
    }

    /**
     * Удаление связи устройства и лицензии по идентификатору.
     *
     * @param id идентификатор связи
     * @return ответ с подтверждением удаления
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            deviceLicenseService.delete(id);
            return ResponseEntity.ok("Лицензия удалена");
        } catch (Exception e) {
            return handleError("Ошибка при удалении связи устройства и лицензии", e);
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

            // Возвращаем успешный ответ со списком связей
            return ResponseEntity.ok(dataDevices); // Статус 200 для успешного запроса
        } catch (Exception e) {
            return handleError("Ошибка при получении списка связей устройства и лицензии", e);
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message + ": " + e.getMessage()); // Статус 500 для внутренней ошибки сервера
    }
}
