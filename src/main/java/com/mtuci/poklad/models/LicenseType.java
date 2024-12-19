package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель типа лицензии.
 * Содержит информацию о типе лицензии, включая описание и длительность по умолчанию.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "LicenseType")
public class LicenseType {

    /**
     * Уникальный идентификатор типа лицензии.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название типа лицензии (например, персональная, корпоративная и т.д.).
     */
    private String name;

    /**
     * Описание типа лицензии.
     */
    @Column(length = 500)
    private String description;

    /**
     * Длительность действия лицензии по умолчанию (например, в днях).
     */
    private Long defaultDuration;

    /**
     * Список лицензий, связанных с этим типом лицензии.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "licenseType")
    private List<License> licenses;
}
