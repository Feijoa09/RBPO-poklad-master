package com.mtuci.poklad.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление статусов истории лицензий.
 */
@Getter
@AllArgsConstructor
public enum LicenseHistoryStatus {

    /**
     * Статус для новых лицензий.
     */
    CREATE("Создана"),

    /**
     * Статус для изменённых лицензий.
     */
    MODIFICATION("Изменена"),

    /**
     * Статус для активированных лицензий.
     */
    ACTIVATE("Активирована"),

    /**
     * Статус для лицензий с ошибками.
     */
    ERROR("Ошибка"),

    /**
     * Статус для истекших лицензий.
     */
    EXPIRED("Истекла");

    /**
     * Человеко-читаемое описание статуса.
     */
    private final String status;

    /**
     * Получить строковое представление статуса.
     *
     * @return строковое представление статуса
     */
    @Override
    public String toString() {
        return status;
    }
}
