package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name = "signatures") // лучше использовать множественное число
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String threatName;


    @Column(name = "first_bytes")
    private String firstBytes; // храним в Base64

    @Setter
    private String remainderHash;

    private int remainderLength;

    private String fileType;

    private int offsetStart;

    private int offsetEnd;


    @Column(name = "digital_signature")
    private String digitalSignature;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String status;

    @Version
    private Long version;

    private String signatureData;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "corrupted")
    private boolean corrupted;

    // region Геттеры и сеттеры

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getThreatName() {
        return threatName;
    }

    public void setThreatName(String threatName) {
        this.threatName = threatName;
    }

    public String getFirstBytes() {
        return firstBytes;
    }

    public void setFirstBytes(byte[] firstBytes) {
        this.firstBytes = Base64.getEncoder().encodeToString(firstBytes);
    }

    public byte[] getFirstBytesAsBytes() {
        return Base64.getDecoder().decode(this.firstBytes);
    }

    public String getRemainderHash() {
        return remainderHash;
    }

    public int getRemainderLength() {
        return remainderLength;
    }

    public void setRemainderLength(int remainderLength) {
        this.remainderLength = remainderLength;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public void setOffsetStart(int offsetStart) {
        this.offsetStart = offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(int offsetEnd) {
        this.offsetEnd = offsetEnd;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getSignatureData() {
        return this.signatureData;
    }

    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
    }

    public boolean isCorrupted() {
        return corrupted;
    }

    public void setCorrupted(boolean corrupted) {
        this.corrupted = corrupted;
    }

    // endregion

    // Автоматическое обновление времени при изменении записи
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }
}
