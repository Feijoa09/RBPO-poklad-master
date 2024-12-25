package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.License;
import com.mtuci.poklad.models.LicenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
}
