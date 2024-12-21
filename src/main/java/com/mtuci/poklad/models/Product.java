package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Модель продукта, который может быть лицензирован.
 * Содержит информацию о продукте, включая его название и статус блокировки.
 */
@Entity
@Data
@Table(name = "Product")

public class Product {

    /**
     * Уникальный идентификатор продукта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название продукта.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Статус блокировки продукта (активен/неактивен).
     */
    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    /**
     * Список лицензий, связанных с этим продуктом.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
    private List<License> licenses;
}