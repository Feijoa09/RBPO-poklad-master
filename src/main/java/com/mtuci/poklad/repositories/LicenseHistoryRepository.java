package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.LicenseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
}
