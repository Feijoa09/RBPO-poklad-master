package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "signature_history")
public class SignatureHistory {

    @Id
    @GeneratedValue
    private UUID historyId;

    private UUID signatureId;

    private LocalDateTime versionCreatedAt;

    private String threatName;

    private String firstBytes;

    private String remainderHash;

    private int remainderLength;

    private String fileType;

    private int offsetStart;

    private int offsetEnd;

    @Column(length = 512)
    private String digitalSignature;

    private String status;

    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
