package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.SignatureAudit;
import com.mtuci.poklad.repositories.SignatureAuditRepository;
import com.mtuci.poklad.service.SignatureAuditService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SignatureAuditServiceImpl implements SignatureAuditService {

    private final SignatureAuditRepository signatureAuditRepository;

    public SignatureAuditServiceImpl(SignatureAuditRepository signatureAuditRepository) {
        this.signatureAuditRepository = signatureAuditRepository;
    }

    @Override
    public List<SignatureAudit> getAuditsForSignatures(List<UUID> signatureIds) {
        // Поиск записей в таблице аудита для всех указанных GUID
        return signatureAuditRepository.findBySignatureIdIn(signatureIds);
    }
}
