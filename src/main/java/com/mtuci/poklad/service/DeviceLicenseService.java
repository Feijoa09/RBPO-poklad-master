package com.mtuci.poklad.service;

import com.mtuci.poklad.models.DeviceLicense;
import com.mtuci.poklad.requests.DataDeviceLicenseRequest;

import java.sql.Date;
import java.util.List;

public interface DeviceLicenseService {
    DeviceLicense save(Long deviceId, Long licenseId, Date activationDate);

    // получение всех
    List<DeviceLicense> getAll();

    // обновление
    DeviceLicense update(Long id, Long deviceId, Long licenseId, Date activationDate);

    // удаление
    void delete(Long id);
}
