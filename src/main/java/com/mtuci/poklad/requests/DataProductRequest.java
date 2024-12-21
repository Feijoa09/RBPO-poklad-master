package com.mtuci.poklad.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataProductRequest {
    private Long id;           // Идентификатор продукта
    private String name;       // Название продукта
    private boolean isBlocked;   // Статус блокировки
}
