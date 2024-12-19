package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.models.DeviceLicense;
import com.mtuci.poklad.models.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {

    /**
     * Метод для поиска записи DeviceLicense по Device и License.
     *
     * @param device Устройство
     * @param license Лицензия
     * @return Опционально найденная запись DeviceLicense
     */
    Optional<DeviceLicense> findByDeviceAndLicense(Device device, License license);
}
