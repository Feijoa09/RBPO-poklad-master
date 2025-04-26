package com.mtuci.poklad.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "signature_history")
public class SignatureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private UUID signatureId;

    private LocalDateTime versionCreatedAt;

    private String threatName;

    private String firstBytes;

    private String remainderHash;

    private int remainderLength;

    private String fileType;

    private int offsetStart;

    private int offsetEnd;

    @Lob
    private String digitalSignature;

    private String status;

    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // Геттеры и сеттеры

    public Long getHistoryId() {
        return historyId;
    }
    private String changedBy;

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public UUID getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(UUID signatureId) {
        this.signatureId = signatureId;
    }

    public LocalDateTime getVersionCreatedAt() {
        return versionCreatedAt;
    }

    public void setVersionCreatedAt(LocalDateTime versionCreatedAt) {
        this.versionCreatedAt = versionCreatedAt;
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

    public void setFirstBytes(String firstBytes) {
        this.firstBytes = firstBytes;
    }

    public String getRemainderHash() {
        return remainderHash;
    }

    public void setRemainderHash(String remainderHash) {
        this.remainderHash = remainderHash;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
