package com.mtuci.poklad.requests;

import lombok.Data;

@Data
public class DeviceInfoRequest {
    private String name, macAddress;
}
