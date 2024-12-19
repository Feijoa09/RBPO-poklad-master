package com.mtuci.poklad.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление разрешений, которые могут быть предоставлены пользователю.
 * Каждое разрешение соответствует определенной операции, такой как чтение или изменение данных.
 */
@Getter
@AllArgsConstructor
public enum Permission {

    /**
     * Разрешение на чтение данных.
     */
    READ("read"),

    /**
     * Разрешение на изменение данных.
     */
    MODIFICATION("modification");

    /**
     * Строковое значение разрешения.
     */
    private final String permission;

    /**
     * Получить строковое представление разрешения.
     *
     * @return строковое представление разрешения
     */
    @Override
    public String toString() {
        return permission;
    }

    /**
     * Проверяет, является ли данное разрешение равным переданному значению.
     *
     * @param permission строковое значение разрешения для проверки
     * @return true, если разрешение совпадает с переданным значением, иначе false
     */
    public static boolean contains(String permission) {
        for (Permission perm : values()) {
            if (perm.getPermission().equals(permission)) {
                return true;
            }
        }
        return false;
    }
}
