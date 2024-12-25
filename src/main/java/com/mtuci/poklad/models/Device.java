package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель устройства, которое может быть связано с пользователем и лицензиями.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Device")
public class Device {

    /**
     * Уникальный идентификатор устройства.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название устройства.
     */
    private String name;

    /**
     * MAC-адрес устройства.
     */
    private String macAddress;

    /**
     * Пользователь, которому принадлежит устройство.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    /**
     * Лицензии, связанные с устройством.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "device", cascade = CascadeType.ALL)
    private List<DeviceLicense> deviceLicenses;
}
