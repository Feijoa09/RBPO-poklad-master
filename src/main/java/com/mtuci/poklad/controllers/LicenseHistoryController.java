package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.LicenseHistory;
import com.mtuci.poklad.requests.DataLicenseHistoryRequest;
import com.mtuci.poklad.service.impl.LicenseHistoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage/licenseHistory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LicenseHistoryController {

    private final LicenseHistoryServiceImpl licenseHistoryService;

    /**
     * Сохранение истории лицензии.
     *
     * @param request данные истории лицензии для сохранения
     * @return ответ с созданной историей и статусом CREATED
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam DataLicenseHistoryRequest request) {
        try {
            LicenseHistory licenseHistory = licenseHistoryService.save(request);
            request.setId(licenseHistory.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (Exception e) {
            return handleError("Ошибка при сохранении истории лицензии", e);
        }
    }

    /**
     * Получение всех записей истории лицензий.
     *
     * @return список всех записей истории лицензий
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<LicenseHistory> licenseHistories = licenseHistoryService.getAll();
            List<DataLicenseHistoryRequest> data = licenseHistories.stream()
                    .map(licenseHistory -> new DataLicenseHistoryRequest(
                            licenseHistory.getId(),
                            licenseHistory.getLicense().getId(),
                            licenseHistory.getUser() == null ? null : licenseHistory.getUser().getId(),
                            licenseHistory.getStatus(),
                            licenseHistory.getDescription(),
                            licenseHistory.getChangeDate()
                    ))
                    .toList();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return handleError("Ошибка при получении данных истории лицензий", e);
        }
    }

    /**
     * Обновление истории лицензии.
     *
     * @param request новые данные для истории лицензии
     * @return ответ с обновленными данными
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam DataLicenseHistoryRequest request) {
        try {
            licenseHistoryService.update(request);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return handleError("Ошибка при обновлении истории лицензии", e);
        }
    }

    /**
     * Удаление записи истории лицензии.
     *
     * @param id идентификатор записи для удаления
     * @return ответ с сообщением об успешном удалении
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            licenseHistoryService.delete(id);
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
     * @return ответ с сообщением об ошибке
     */
    private ResponseEntity<?> handleError(String message, Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message + ": " + e.getMessage());
    }
}
