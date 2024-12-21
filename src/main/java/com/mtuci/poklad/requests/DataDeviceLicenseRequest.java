package com.mtuci.poklad.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Класс запроса для передачи данных о связи устройства и лицензии.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDeviceLicenseRequest {
    private Long id; // Идентификатор связи
    private Long deviceId; // Идентификатор устройства
    private Long licenseId; // Идентификатор лицензии
    private Date activationDate; // Дата активации лицензии на устройстве
}
