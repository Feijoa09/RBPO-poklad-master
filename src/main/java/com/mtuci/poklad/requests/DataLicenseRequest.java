package com.mtuci.poklad.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class DataLicenseRequest {
    private Long id;
    private Long licenseTypeId;
    private Long productId;
    private Long userId;
    private Long ownerId;
    private Date firstActivationDate;
    private Date endingDate;
    private boolean isBlocked;
    private int deviceCount;
    private Long duration;
    private String code;
    private String description;

    // Новый конструктор, если нужно добавить дополнительные параметры
    public DataLicenseRequest(Long id, Long licenseTypeId, Long productId, Long userId, Long ownerId, Date firstActivationDate, Date endingDate, boolean isBlocked, int deviceCount, Long duration, String code, String description, boolean newFlag, Integer newValue) {
        this.id = id;
        this.licenseTypeId = licenseTypeId;
        this.productId = productId;
        this.userId = userId;
        this.ownerId = ownerId;
        this.firstActivationDate = firstActivationDate;
        this.endingDate = endingDate;
        this.isBlocked = isBlocked;
        this.deviceCount = deviceCount;
        this.duration = duration;
        this.code = code;
        this.description = description;
        // Дополнительная логика для новых параметров
    }
}
