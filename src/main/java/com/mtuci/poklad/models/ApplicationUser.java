package com.mtuci.poklad.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class ApplicationUser {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Уникальный логин пользователя.
     */
    @Column(unique = true, nullable = false)
    private String login;

    // Сеттер для passwordHash (если нужно)
    /**
     * Хэш пароля пользователя.
     */
    @Setter
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Уникальный email пользователя.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Роль пользователя в системе.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private ApplicationRole role;




    /**
     * Лицензии, где пользователь является владельцем.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)

    private List<License> ownedLicenses;

    /**
     * Лицензии, где пользователь является пользователем.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)

    private List<License> userLicenses;

    /**
     * История лицензий, связанная с пользователем.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)

    private List<LicenseHistory> licenseHistories;

    /**
     * Устройства, зарегистрированные за пользователем.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Device> devices;



}

