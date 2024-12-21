package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.models.DeviceLicense;
import com.mtuci.poklad.models.License;
import com.mtuci.poklad.repositories.DeviceLicenseRepository;
import com.mtuci.poklad.repositories.DeviceRepository;
import com.mtuci.poklad.repositories.LicenseRepository;
import com.mtuci.poklad.service.DeviceLicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceLicenseServiceImpl implements DeviceLicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;
    private final DeviceRepository deviceRepository;
    private final LicenseRepository licenseRepository;

    @Override
    public List<DeviceLicense> getAll() {
        return deviceLicenseRepository.findAll();
    }

    @Override
    public DeviceLicense save(Long id, Long deviceId, Long licenseId, Date activationDate) {
        // Найти устройство
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено"));

        // Найти лицензию
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("Лицензия не найдена"));

        // Создать связь устройства и лицензии
        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setDevice(device);
        deviceLicense.setLicense(license);
        deviceLicense.setActivationDate(activationDate);

        return deviceLicenseRepository.save(deviceLicense);
    }

    @Override
    public DeviceLicense update(Long id, Long deviceId, Long licenseId, Date activationDate) {
        // Найти существующую запись
        DeviceLicense deviceLicense = deviceLicenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Связь не найдена"));

        // Найти устройство
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено"));

        // Найти лицензию
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("Лицензия не найдена"));

        // Обновить запись
        deviceLicense.setDevice(device);
        deviceLicense.setLicense(license);
        deviceLicense.setActivationDate(activationDate);

        return deviceLicenseRepository.save(deviceLicense);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Проверить существование записи
        if (!deviceLicenseRepository.existsById(id)) {
            throw new RuntimeException("Связь не найдена");
        }

        // Удалить запись
        deviceLicenseRepository.deleteById(id);
    }


}
