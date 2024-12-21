package com.mtuci.poklad.service;

import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.License;
import com.mtuci.poklad.models.LicenseHistory;
import com.mtuci.poklad.requests.DataLicenseHistoryRequest;

import java.util.List;
import java.util.Optional;

public interface LicenseHistoryService {
    boolean recordLicenseChange(
            License license, ApplicationUser owner,
            String status, String description);
    Optional<LicenseHistory> findById(Long id);

    // сохранение
    LicenseHistory save(DataLicenseHistoryRequest request);

    // получение всех
    List<LicenseHistory> getAll();

    // обновление
    LicenseHistory update(DataLicenseHistoryRequest request);

    // удаление
    void delete(Long id);
}
