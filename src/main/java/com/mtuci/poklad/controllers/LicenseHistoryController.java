package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.LicenseHistory;
import com.mtuci.poklad.requests.DataLicenseHistoryRequest;
import com.mtuci.poklad.service.impl.LicenseHistoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@RestController
@RequestMapping("/settings/licenseHistory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LicenseHistoryController {

    private final LicenseHistoryServiceImpl licenseHistoryService;

    /**
     * Сохранение истории лицензии.
     *
     * @param licenseId   идентификатор лицензии (тип Long) - нужен для связи истории с конкретной лицензией
     * @param userId      идентификатор пользователя (тип Long) - нужен для связи истории с конкретным пользователем
     * @param status      статус лицензии (тип String) - хранит статус лицензии в момент изменения
     * @param description описание изменения (тип String) - текстовое описание произошедшего изменения
     * @param changeDate  дата изменения (тип String) - строка, представляющая дату изменения в формате 'yyyy-MM-dd'
     * @return ResponseEntity<?> ответ с созданной историей лицензии и статусом CREATED (код 201)
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam("licenseId") Long licenseId,   // Идентификатор лицензии
                                  @RequestParam Long userId,                   // Идентификатор пользователя
                                  @RequestParam String status,                 // Статус лицензии
                                  @RequestParam String description,            // Описание изменения
                                  @RequestParam String changeDate) {           // Дата изменения как строка
        try {
            // Преобразуем строку в java.sql.Date
            java.sql.Date date = java.sql.Date.valueOf(changeDate);

            // Создаем объект запроса
            DataLicenseHistoryRequest request = new DataLicenseHistoryRequest(
                    null, // id оставляем null, оно генерируется на уровне базы данных
                    licenseId,
                    userId,
                    status,
                    description,
                    date // Используем date вместо строки changeDate
            );

            // Передаем объект в метод save
            LicenseHistory licenseHistory = licenseHistoryService.save(request);

            // Возвращаем успешный ответ
            return ResponseEntity.status(HttpStatus.CREATED).body(licenseHistory);
        } catch (IllegalArgumentException e) {
            // Если формат даты неверный
            return handleError("Некорректный формат даты. Используйте формат yyyy-MM-dd", e);
        } catch (Exception e) {
            // Обработка других ошибок
            return handleError("Ошибка при сохранении истории лицензии", e);
        }
    }


    /**
     * Получение всех записей истории лицензий.
     *
     * @return ResponseEntity<?> список всех записей истории лицензий с кодом 200
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<LicenseHistory> licenseHistories = licenseHistoryService.getAll();  // Получаем все записи истории
            return ResponseEntity.ok(licenseHistories); // Статус 200 для успешного запроса
        } catch (Exception e) {
            return handleError("Ошибка при получении данных истории лицензий", e);
        }
    }

    /**
     * Обновление истории лицензии.
     *
     * @param id            идентификатор записи истории лицензии (тип Long) - нужен для поиска и обновления истории
     * @param licenseId     идентификатор лицензии (тип Long) - нужен для связывания с лицензией
     * @param userId        идентификатор пользователя (тип Long) - для связывания с пользователем
     * @param status        новый статус лицензии (тип String)
     * @param description   новое описание изменения (тип String)
     * @param changeDateStr новая дата изменения в формате 'yyyy-MM-dd' (тип String)
     * @return ResponseEntity<?> ответ с обновленной историей лицензии
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam Long id,                     // Идентификатор записи истории
                                    @RequestParam Long licenseId,              // Идентификатор лицензии
                                    @RequestParam Long userId,                 // Идентификатор пользователя
                                    @RequestParam String status,               // Новый статус лицензии
                                    @RequestParam String description,          // Новое описание изменения
                                    @RequestParam String changeDateStr) {      // Новая дата изменения (String)
        try {
            // Преобразуем строку в объект Date с использованием SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  // Указываем формат, например: "yyyy-MM-dd"
            Date changeDate = dateFormat.parse(changeDateStr);  // Преобразуем строку в объект Date

            // Создаем объект DataLicenseHistoryRequest
            DataLicenseHistoryRequest request = new DataLicenseHistoryRequest(
                    id,          // Идентификатор записи
                    licenseId,   // Идентификатор лицензии
                    userId,      // Идентификатор пользователя
                    status,      // Статус
                    description, // Описание
                    changeDate   // Дата изменения
            );

            // Передаем объект в метод update
            LicenseHistory updatedHistory = licenseHistoryService.update(request);

            return ResponseEntity.ok(updatedHistory);  // Возвращаем обновленную запись
        } catch (ParseException e) {
            // Если формат даты неверный, возвращаем ошибку с подробным сообщением
            return ResponseEntity.badRequest().body("Ошибка: неверный формат даты. Используйте формат 'yyyy-MM-dd'.");
        } catch (Exception e) {
            // Обработка других ошибок
            return handleError("Ошибка при обновлении истории лицензии", e);
        }
    }

    /**
     * Удаление записи истории лицензии.
     *
     * @param id идентификатор записи для удаления (тип Long)
     * @return ResponseEntity<?> ответ с сообщением об успешном удалении
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) { // Идентификатор записи для удаления
        try {
            licenseHistoryService.delete(id);  // Вызываем метод для удаления
            return ResponseEntity.ok("История лицензии успешно удалена");
        } catch (Exception e) {
            return handleError("Ошибка при удалении истории лицензии", e);
        }
    }

    /**
     * Универсальный метод для обработки ошибок.
     *
     * @param message сообщение для ошибки
     * @param e исключение
     * @return ResponseEntity<?> ответ с сообщением об ошибке
     */
    private ResponseEntity<?> handleError(String message, Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message + ": " + e.getMessage());
    }
}

