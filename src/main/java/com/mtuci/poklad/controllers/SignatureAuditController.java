package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.SignatureAudit;
import com.mtuci.poklad.service.SignatureAuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit")
public class SignatureAuditController {

    private final SignatureAuditService signatureAuditService;

    public SignatureAuditController(SignatureAuditService signatureAuditService) {
        this.signatureAuditService = signatureAuditService;
    }

    /**
     * Получить аудит для списка сигнатур.
     *
     * @param signatureIds Список GUID сигнатур.
     * @return Список записей аудита.
     */
    @GetMapping("/getAudits")
    public ResponseEntity<List<SignatureAudit>> getAudits(@RequestParam List<UUID> signatureIds) {
        List<SignatureAudit> audits = signatureAuditService.getAuditsForSignatures(signatureIds);
        return ResponseEntity.ok(audits);
    }
}
