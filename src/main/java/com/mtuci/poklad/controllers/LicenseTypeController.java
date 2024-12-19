package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.LicenseType;
import com.mtuci.poklad.requests.DataLicenseTypeRequest;
import com.mtuci.poklad.service.impl.LicenseTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/licenseType")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LicenseTypeController {

    private final LicenseTypeServiceImpl licenseTypeService;

    /**
     * Сохранение нового типа лицензии.
     *
     * @param id              идентификатор типа лицензии (для обновления)
     * @param name            название типа лицензии
     * @param description     описание типа лицензии
     * @param defaultDuration длительность лицензии по умолчанию
     * @return ответ с данными созданного типа лицензии
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam(required = false) Long id,
                                  @RequestParam String name,
                                  @RequestParam String description,
                                  @RequestParam Long defaultDuration) {
        try {
            // Проверяем, что параметры не null и не пустые
            if (name == null || name.isEmpty() || description == null || description.isEmpty() || defaultDuration == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Параметры не могут быть пустыми");
            }

            // Создаем объект запроса с id (если передан)
            DataLicenseTypeRequest request = new DataLicenseTypeRequest(id, name, description, defaultDuration);

            LicenseType license;
            if (id == null) {
                // Если id нет, создаем новый объект
                license = licenseTypeService.save(request);
                request.setId(license.getId());  // Устанавливаем ID после сохранения
                return ResponseEntity.status(HttpStatus.CREATED).body(request); // Код 201 для успешного создания
            } else {
                // Если id есть, обновляем существующий объект
                licenseTypeService.update(request);
                return ResponseEntity.ok(request); // Код 200 для успешного обновления
            }

        } catch (Exception e) {
            return handleError("Ошибка при сохранении типа лицензии", e);
        }
    }

    /**
     * Получение списка всех типов лицензий.
     *
     * @return список типов лицензий
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            var licenseTypes = licenseTypeService.getAll();
            return ResponseEntity.ok(licenseTypes); // Код 200 для успешного запроса
        } catch (Exception e) {
            return handleError("Ошибка при получении списка типов лицензий", e);
        }
    }

    /**
     * Обновление типа лицензии.
     *
     * @param id              идентификатор типа лицензии для обновления
     * @param name            новое название типа лицензии
     * @param description     новое описание типа лицензии
     * @param defaultDuration новая длительность лицензии по умолчанию
     * @return ответ с обновленным типом лицензии
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam Long id,
                                    @RequestParam String name,
                                    @RequestParam String description,
                                    @RequestParam Long defaultDuration) {
        try {
            // Создаем объект запроса
            DataLicenseTypeRequest request = new DataLicenseTypeRequest(id, name, description, defaultDuration);
            licenseTypeService.update(request);
            return ResponseEntity.ok(request); // Код 200 для успешного обновления
        } catch (Exception e) {
            return handleError("Ошибка при обновлении типа лицензии", e);
        }
    }

    /**
     * Удаление типа лицензии.
     *
     * @param id идентификатор типа лицензии для удаления
     * @return ответ с сообщением о успешном удалении
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            licenseTypeService.delete(id);
            return ResponseEntity.ok("Тип лицензии удалён"); // Код 200 для успешного удаления
        } catch (Exception e) {
            return handleError("Ошибка при удалении типа лицензии", e);
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message + ": " + e.getMessage());
    }
}
