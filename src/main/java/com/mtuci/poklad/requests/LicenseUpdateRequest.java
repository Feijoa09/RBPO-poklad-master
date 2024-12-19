package com.mtuci.poklad.requests;

import lombok.Data;

@Data
public class LicenseUpdateRequest {
    private String password, codeActivation;
}
