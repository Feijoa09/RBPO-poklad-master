package com.mtuci.poklad.service;

import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.models.License;
import com.mtuci.poklad.models.Ticket;
import com.mtuci.poklad.requests.DataLicenseRequest;

import java.util.List;

public interface LicenseService {

    License createLicense(
            Long productId,
            Long ownerId,
            Long licenseTypeId,
            Integer deviceCount,
            Long duration,
            String firstActivationDate,
            String endingDate,
            boolean blocked,
            String code,
            String description
    );



    Ticket activateLicense(String activationCode, Device device, ApplicationUser user);
    Ticket generateTicket(License license, Device device, String description);
    List<Ticket> licenseRenewal(String activationCode, ApplicationUser user);

    boolean validateLicense(License license, Device device, ApplicationUser user);

    void updateLicense(License license);

    List<License> getActiveLicensesForDevice(Device device, ApplicationUser user);

    // сохранение
    License save(DataLicenseRequest request);

    // получение всех
    List<License> getAll();

    // обновление
    License update(DataLicenseRequest request);

    // удаление
    void delete(Long id);
}
