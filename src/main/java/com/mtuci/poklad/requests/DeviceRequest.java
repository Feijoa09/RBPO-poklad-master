package com.mtuci.poklad.requests;

import lombok.Data;

@Data
public class DeviceRequest {
    private String activationCode, name, macAddress;
}
