package com.mtuci.poklad.service;

import com.mtuci.poklad.models.DeviceLicense;

import java.sql.Date;
import java.util.List;

public interface DeviceLicenseService {

    DeviceLicense saveDeviceLicense(DeviceLicense deviceLicense);

    // получение всех
    List<DeviceLicense> getAll();

    DeviceLicense save(Long id, Long deviceId, Long licenseId, Date activationDate);

    // обновление
    DeviceLicense update(Long id, Long deviceId, Long licenseId, Date activationDate);

    // удаление
    void delete(Long id);
}
