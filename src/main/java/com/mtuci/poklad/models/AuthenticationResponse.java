package com.mtuci.poklad.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ, содержащий информацию для аутентификации.
 * Содержит токен и логин пользователя.
 */
@Data
@AllArgsConstructor

public class AuthenticationResponse {

    /**
     * JWT токен, используемый для аутентификации пользователя.
     */
    private String token;

    /**
     * Логин пользователя.
     */
    private String login;
}
