package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "License")
public class License {

    /**
     * Уникальный идентификатор лицензии.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата первого активации лицензии.
     */
    private Date firstActivationDate;

    /**
     * Дата окончания действия лицензии.
     */
    private Date endingDate;

    /**
     * Статус блокировки лицензии.
     */
    private boolean isBlocked;

    /**
     * Количество устройств, на которые можно установить лицензию.
     */
    private Integer deviceCount;

    /**
     * Длительность действия лицензии в днях.
     */
    private Long Duration;

    /**
     * Уникальный код лицензии.
     */
    private String code;

    /**
     * Описание лицензии.
     */
    @Column(length = 500)
    private String description;

    /**
     * Тип лицензии (например, персональная, корпоративная и т.д.).
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "type_id")
    private LicenseType licenseType;

    /**
     * Продукт, для которого выдана лицензия.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Пользователь, которому принадлежит лицензия.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    /**
     * Владелец лицензии (может отличаться от пользователя).
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id")
    private ApplicationUser owner;

    /**
     * История изменений лицензии (например, активации, продления).
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "license")
    private List<LicenseHistory> licenseHistories;

    /**
     * Лицензии, связанные с устройствами.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "license")
    private List<DeviceLicense> deviceLicenses;
}
