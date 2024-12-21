package com.mtuci.poklad.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataLicenseTypeRequest {
    private Long id;
    private String name, description;
    private Long defaultDuration;

    public DataLicenseTypeRequest(String name, String description, Long defaultDuration) {
    }
}
