package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.sql.Date;

/**
 * Модель, которая связывает устройство с лицензией.
 * Представляет информацию о лицензии, активированной на конкретном устройстве.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "device_license")
public class DeviceLicense {

    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата активации лицензии на устройстве.
     */

    private Date activationDate;

    /**
     * Устройство, на котором активирована лицензия.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    /**
     * Лицензия, связанная с устройством.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "license_id", nullable = false)
    private License license;
}
