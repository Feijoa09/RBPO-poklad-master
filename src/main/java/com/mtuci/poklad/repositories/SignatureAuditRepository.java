package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.SignatureAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureAuditRepository extends JpaRepository<SignatureAudit, UUID> {

    /**
     * Найти все записи аудита по списку GUID.
     *
     * @param signatureIds Список GUID сигнатур.
     * @return Список записей аудита.
     */
    List<SignatureAudit> findBySignatureIdIn(List<UUID> signatureIds);
}
