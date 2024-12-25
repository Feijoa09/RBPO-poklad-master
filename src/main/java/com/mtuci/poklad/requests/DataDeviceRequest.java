package com.mtuci.poklad.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataDeviceRequest {

    private Long id;
    private Long userId;
    private String name;
    private String macAddress;
}
