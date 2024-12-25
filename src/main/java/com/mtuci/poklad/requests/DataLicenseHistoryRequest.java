package com.mtuci.poklad.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DataLicenseHistoryRequest {
    private Long id;
    private Long licenseId;
    private Long userId;
    private String status;
    private String description;
    private Date changeDate;
}
