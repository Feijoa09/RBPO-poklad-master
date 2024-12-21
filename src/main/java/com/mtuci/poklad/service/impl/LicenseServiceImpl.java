package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.*;
import com.mtuci.poklad.repositories.LicenseRepository;
import com.mtuci.poklad.repositories.LicenseTypeRepository;
import com.mtuci.poklad.repositories.ProductRepository;
import com.mtuci.poklad.repositories.UserRepository;
import com.mtuci.poklad.requests.DataLicenseRequest;
import com.mtuci.poklad.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {
    private final LicenseRepository licenseRepository;
    private final ProductServiceImpl productService;
    private final UserServiceImpl userService;
    private final LicenseTypeServiceImpl licenseTypeService;
    private final LicenseHistoryServiceImpl licenseHistoryService;
    private final DeviceLicenseServiceImpl deviceLicenseService;

    @Override
    public License createLicense(Long productId, Long ownerId, Long licenseTypeId, Integer deviceCount,
                                 Long duration, String firstActivationDate, String endingDate,
                                 boolean isBlocked, String code, String description) {
        // Получаем зависимости через сервисы
        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId)
                .orElseThrow(() -> new RuntimeException("Тип лицензии не найден"));
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт не найден"));
        ApplicationUser owner = userService.getUserById(ownerId)
                .orElseThrow(() -> new RuntimeException("Владелец не найден"));

        // Преобразуем строки в объекты типа Date
        Date firstActivation = Date.valueOf(firstActivationDate);
        Date ending = Date.valueOf(endingDate);

        // Создаем новый объект License
        License license = new License();
        license.setProduct(product); // Используем объект product
        license.setOwner(owner);     // Используем объект owner
        license.setLicenseType(licenseType); // Используем объект licenseType
        license.setDeviceCount(deviceCount);
        license.setDuration(duration);
        license.setFirstActivationDate(firstActivation);
        license.setEndingDate(ending);
        license.setBlocked(isBlocked);
        license.setCode(code);
        license.setDescription(description);

        // Сохраняем лицензию в базе данных
        return licenseRepository.save(license);
    }

    @Override
    public Ticket activateLicense(String activationCode, Device device, ApplicationUser user) {
        License license = licenseRepository.findByCode(activationCode).orElseThrow(
                () -> new RuntimeException("Лицензия не найдена"));

        if (!validateLicense(license, device, user)) {
            licenseHistoryService.recordLicenseChange(license, user, LicenseHistoryStatus.ERROR.name(), "Активация лицензии невозможна");
            throw new RuntimeException("Активация невозможна");
        }

        if (license.getUser() == null)
            license.setUser(user);
        updateLicense(license);

        licenseHistoryService.recordLicenseChange(license, user, LicenseHistoryStatus.ACTIVATE.name(), "Лицензия успешно активирована");
        return generateTicket(license, device, "Лицензия активирована");
    }

    @Override
    public boolean validateLicense(License license, Device device, ApplicationUser user) {
        // проверка условий активации лицензии
        return !license.isBlocked() &&
                (license.getUser() == null || license.getUser().getId().equals(user.getId())) &&
                license.getDeviceLicenses().stream().noneMatch(deviceLicense ->
                        deviceLicense.getDevice().getId().equals(device.getId()) &&
                                deviceLicense.getLicense().getId().equals(license.getId())) &&
                license.getDeviceLicenses().size() < license.getDeviceCount() &&
                new Date(System.currentTimeMillis()).before(license.getEndingDate());
    }

    @Override
    public void updateLicense(License license) {
        if (license.getFirstActivationDate() == null)
            license.setFirstActivationDate(new Date(System.currentTimeMillis()));

        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        license.setDescription(license.getDescription() + String.format("Пользователь: %s\n" +
                                "Впервые активирована: %s\nАктивированных устройств: %d",
                        license.getUser().getLogin(),
                        formatter.format(license.getFirstActivationDate()),
                        license.getDeviceLicenses().size() + 1
                )
        );

        license = licenseRepository.save(license);
        licenseHistoryService.recordLicenseChange(license, license.getUser(), LicenseHistoryStatus.MODIFICATION.name(), license.getDescription());
    }

    @Override
    public List<License> getActiveLicensesForDevice(Device device, ApplicationUser user) {
        // получение активных лицензий для устройства
        return device.getDeviceLicenses().stream()
                .map(DeviceLicense::getLicense)
                .filter(license ->
                        license.getUser().getId().equals(user.getId()) &&
                                !license.isBlocked() &&
                                license.getEndingDate().after(new Date(System.currentTimeMillis()))
                ).toList();
    }

    private License edit(License license, DataLicenseRequest request) {
        license.setLicenseType(licenseTypeService.getLicenseTypeById(request.getLicenseTypeId()).orElseThrow(
                () -> new RuntimeException("Тип лицензии не найден")
        ));
        license.setProduct(productService.getProductById(request.getProductId()).orElseThrow(
                () -> new RuntimeException("Продукт не найден")
        ));
        license.setUser(userService.getUserById(request.getUserId()).orElseThrow(
                () -> new RuntimeException("Пользователь не найден")
        ));
        license.setOwner(userService.getUserById(request.getOwnerId()).orElseThrow(
                () -> new RuntimeException("Владелец не найден")
        ));
        license.setFirstActivationDate(request.getFirstActivationDate());
        license.setEndingDate(request.getEndingDate());
        license.setBlocked(request.isBlocked());
        license.setDeviceCount(request.getDeviceCount());
        license.setDuration(request.getDuration());
        license.setDescription(request.getDescription());
        return license;
    }

    @Override
    public License save(DataLicenseRequest request) {
        return licenseRepository.save(edit(new License(), request));
    }

    @Override
    public List<License> getAll() {
        return licenseRepository.findAll();
    }

    @Override
    public License update(DataLicenseRequest request) {
        License license = licenseRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException("Лицензия не найдена")
        );
        return licenseRepository.save(edit(license, request));
    }

    @Override
    public void delete(Long id) {
        licenseRepository.deleteById(id);
    }

    @Override
    public Ticket generateTicket(License license, Device device, String description) {
        Ticket ticket = new Ticket();

        // преобразование java.sql.Date в LocalDate
        ticket.setNowDate(license.getFirstActivationDate().toLocalDate());
        ticket.setActivationDate(license.getFirstActivationDate().toLocalDate());
        ticket.setExpirationDate(license.getEndingDate().toLocalDate());

        ticket.setExpiration(license.getDuration());
        ticket.setUserID(license.getUser().getId());
        ticket.setDeviceID(device.getId());

        // устанавливаем значение блокировки лицензии
        ticket.setBlockedLicense(license.isBlocked()); // используем isBlockedLicense

        ticket.setDescription(description);

        // генерация цифровой подписи
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String ds = bCryptPasswordEncoder.encode(
                ticket.getNowDate().toString() + ticket.getActivationDate().toString() +
                        ticket.getExpirationDate().toString() + ticket.getExpiration().toString() +
                        ticket.getUserID().toString() + ticket.getDeviceID().toString()
        );
        ticket.setDigitalSignature(ds);

        return ticket;
    }

    @Override
    public List<Ticket> licenseRenewal(String activationCode, ApplicationUser user) {
        // проверка ключа лицензии
        License license = licenseRepository.findByCode(activationCode).orElseThrow(
                () -> new RuntimeException("Ключ лицензии недействителен")
        );

        List<Ticket> tickets = license.getDeviceLicenses().stream()
                .map(deviceLicense -> generateTicket(license, deviceLicense.getDevice(), "")).toList();

        // проверка возможности продления
        if (
                license.isBlocked() ||
                        license.getEndingDate().before(new Date(System.currentTimeMillis())) ||
                        (license.getUser() != null && !license.getUser().getId().equals(user.getId()))
        ) {
            tickets.forEach(ticket -> {
                ticket.setDescription("Невозможно продлить лицензию");
                licenseHistoryService.recordLicenseChange(license, user, LicenseHistoryStatus.ERROR.name(), ticket.getDescription());
            });
            return tickets;
        }

        // продление на год
        license.setDuration(license.getLicenseType().getDefaultDuration());
        license.setEndingDate(new Date(System.currentTimeMillis() + license.getDuration() * 1000));

        tickets.forEach(ticket -> {
            ticket.setDescription("Лицензия успешно продлена");
            licenseHistoryService.recordLicenseChange(license, user, LicenseHistoryStatus.MODIFICATION.name(), ticket.getDescription());
        });
        return tickets;
    }

    private String generateCodeLicense(Long productId, Long ownerId, Long licenseTypeId, Integer deviceCount) {
        // создаем строку для кодирования
        String codeString = productId + ownerId.toString() + licenseTypeId.toString() + deviceCount.toString();

        // генерируем код, используя BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(codeString);
    }

}
