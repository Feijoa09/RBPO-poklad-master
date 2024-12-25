package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.License;
import com.mtuci.poklad.models.LicenseHistory;
import com.mtuci.poklad.repositories.LicenseHistoryRepository;
import com.mtuci.poklad.repositories.LicenseRepository;
import com.mtuci.poklad.requests.DataLicenseHistoryRequest;
import com.mtuci.poklad.service.LicenseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LicenseHistoryServiceImpl implements LicenseHistoryService {
    private final LicenseHistoryRepository licenseHistoryRepository;
    private final LicenseRepository licenseRepository;
    private final UserServiceImpl userService;

    @Override
    public boolean recordLicenseChange(License license, ApplicationUser owner, String status, String description) {
        LicenseHistory licenseHistory = new LicenseHistory();
        licenseHistory.setLicense(license);
        licenseHistory.setChangeDate(new Date(System.currentTimeMillis()));
        licenseHistory.setUser(owner);
        licenseHistory.setStatus(status);
        licenseHistory.setDescription(description);

        licenseHistoryRepository.save(licenseHistory);
        return true;
    }

    @Override
    public Optional<LicenseHistory> findById(Long id) {
        return licenseHistoryRepository.findById(id);
    }

    /**
     * Метод для обновления сущности LicenseHistory на основе данных из DataLicenseHistoryRequest
     * @param licenseHistory История лицензии
     * @param request Данные для обновления
     * @return обновленная сущность LicenseHistory
     */
    private LicenseHistory edit(LicenseHistory licenseHistory, DataLicenseHistoryRequest request) {
        // Присваиваем лицензии
        License license = licenseRepository.findById(request.getLicenseId()).orElseThrow(
                () -> new RuntimeException("Лицензия не найдена")
        );
        licenseHistory.setLicense(license);

        // Присваиваем пользователю
        ApplicationUser user = userService.getUserById(request.getUserId()).orElseThrow(
                () -> new RuntimeException("Пользователь не найден")
        );
        licenseHistory.setUser(user);

        // Присваиваем остальные данные
        licenseHistory.setStatus(request.getStatus());
        licenseHistory.setDescription(request.getDescription());
        licenseHistory.setChangeDate(new Date(System.currentTimeMillis()));  // Устанавливаем текущую дату изменения

        return licenseHistory;
    }

    /**
     * Сохранение истории лицензии
     * @param request Запрос с данными для сохранения
     * @return Сохраненная запись истории лицензии
     */
    @Override
    public LicenseHistory save(DataLicenseHistoryRequest request) {
        // Создаем новый объект LicenseHistory, заполняем его и сохраняем
        LicenseHistory licenseHistory = new LicenseHistory();
        return licenseHistoryRepository.save(edit(licenseHistory, request));
    }

    /**
     * Получение всех записей истории лицензий
     * @return Список всех историй лицензий
     */
    @Override
    public List<LicenseHistory> getAll() {
        return licenseHistoryRepository.findAll();
    }

    /**
     * Обновление записи истории лицензии
     * @param request Запрос с данными для обновления
     * @return Обновленная запись истории лицензии
     */
    @Override
    public LicenseHistory update(DataLicenseHistoryRequest request) {
        // Извлекаем запись истории лицензии по ID
        LicenseHistory licenseHistory = licenseHistoryRepository.findById(request.getLicenseId()).orElseThrow(
                () -> new RuntimeException("История лицензии не найдена")
        );

        // Обновляем сущность
        return licenseHistoryRepository.save(edit(licenseHistory, request));
    }

    /**
     * Удаление записи истории лицензии
     * @param id Идентификатор записи для удаления
     */
    @Override
    public void delete(Long id) {
        licenseHistoryRepository.deleteById(id);
    }
}
