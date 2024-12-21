package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Модель истории лицензий.
 * Содержит информацию об изменениях в лицензии, таких как статус, описание и дата изменения.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "LicenseHistory")
public class LicenseHistory {

    /**
     * Уникальный идентификатор записи истории.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Статус изменения лицензии (например, активирована, продлена).
     */
    private String status;

    /**
     * Описание изменения лицензии.
     */
    @Column(length = 500)
    private String description;

    /**
     * Дата изменения состояния лицензии.
     */
    private Date changeDate;

    /**
     * Лицензия, к которой относится изменение.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    /**
     * Пользователь, который произвел изменение.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;
}
