package com.mtuci.poklad.service;

import com.mtuci.poklad.models.LicenseType;
import com.mtuci.poklad.requests.DataLicenseTypeRequest;

import java.util.List;
import java.util.Optional;

public interface LicenseTypeService {
    Optional<LicenseType> getLicenseTypeById(Long id);

    // save
    LicenseType save(DataLicenseTypeRequest request);

    // read
    List<LicenseType> getAll();

    // update
    LicenseType update(DataLicenseTypeRequest request);

    // удаление
    void delete(Long id);
}
