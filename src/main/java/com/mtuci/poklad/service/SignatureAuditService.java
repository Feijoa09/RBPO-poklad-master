package com.mtuci.poklad.service;

import com.mtuci.poklad.models.SignatureAudit;

import java.util.List;
import java.util.UUID;

public interface SignatureAuditService {

    /**
     * Получить аудит для списка сигнатур.
     *
     * @param signatureIds Список GUID сигнатур.
     * @return Список записей аудита для каждой сигнатуры.
     */
    List<SignatureAudit> getAuditsForSignatures(List<UUID> signatureIds);
}
