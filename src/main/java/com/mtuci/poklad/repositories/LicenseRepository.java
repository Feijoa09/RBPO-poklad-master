package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.License;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByCode(String licenseCode);
}
