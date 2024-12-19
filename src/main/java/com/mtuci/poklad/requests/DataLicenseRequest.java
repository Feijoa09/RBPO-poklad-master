package com.mtuci.poklad.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class DataLicenseRequest {
    private Long product_id;
    private Long id, type_id, user_id, owner_id;
    private Date first_activation_date, ending_date;
    private boolean blocked;
    private Integer device_count;
    private Long duration;
    private String code, description;

    public DataLicenseRequest(Long id, Long id1, String id2, Object userId, Long id3, Date firstActivationDate, Date endingDate, boolean blocked, Integer deviceCount, Long duration, String code, String description) {
    }
}
