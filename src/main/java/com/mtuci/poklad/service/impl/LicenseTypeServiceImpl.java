package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.LicenseType;
import com.mtuci.poklad.repositories.LicenseTypeRepository;
import com.mtuci.poklad.requests.DataLicenseTypeRequest;
import com.mtuci.poklad.service.LicenseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LicenseTypeServiceImpl implements LicenseTypeService {
    private final LicenseTypeRepository licenseTypeRepository;

    @Override
    public Optional<LicenseType> getLicenseTypeById(Long id) {
        return licenseTypeRepository.findById(id);
    }

    @Override
    @Transactional
    public LicenseType save(DataLicenseTypeRequest request) {
        // Создание нового типа лицензии
        LicenseType licenseType = new LicenseType();
        return licenseTypeRepository.save(edit(licenseType, request));
    }

    @Override
    public List<LicenseType> getAll() {
        return licenseTypeRepository.findAll();
    }

    @Override
    @Transactional
    public LicenseType update(DataLicenseTypeRequest request) {
        // Проверка существования типа лицензии
        LicenseType licenseType = licenseTypeRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Тип лицензии с таким ID не найден"));
        return licenseTypeRepository.save(edit(licenseType, request));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Проверка существования типа лицензии перед удалением
        if (!licenseTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Тип лицензии с таким ID не найден");
        }
        licenseTypeRepository.deleteById(id);
    }

    private LicenseType edit(LicenseType licenseType, DataLicenseTypeRequest request) {
        licenseType.setName(request.getName());
        licenseType.setDescription(request.getDescription());
        licenseType.setDefaultDuration(request.getDefaultDuration());
        return licenseType;
    }
}
