package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "signature_audit")
public class SignatureAudit {
    @Id
    @GeneratedValue
    private UUID auditId;

    private UUID signatureId;

    private Long changedBy;

    private String changeType;

    private LocalDateTime changedAt;

    private String fieldsChanged;

    @Version
    private Long version;
}
