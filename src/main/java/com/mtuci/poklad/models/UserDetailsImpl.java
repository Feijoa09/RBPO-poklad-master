package com.mtuci.poklad.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;



import java.util.List;

/**
 * Реализация интерфейса UserDetails для Spring Security.
 * Используется для создания пользователя с кастомными данными, такими как логин, пароль и права доступа.
 */
@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    /**
     * Логин пользователя.
     */
    private final String username;

    /**
     * Хэшированный пароль пользователя.
     */
    private final String password;

    /**
     * Права доступа пользователя.
     */
    private List<GrantedAuthority> authorities;

    /**
     * Статус активности аккаунта.
     */
    private final boolean isActive;

    /**
     * Метод для проверки, не истек ли срок действия аккаунта.
     *
     * @return true, если аккаунт не истек
     */
    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    /**
     * Метод для проверки, не заблокирован ли аккаунт.
     *
     * @return true, если аккаунт не заблокирован
     */
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    /**
     * Метод для проверки, не истек ли срок действия учетных данных.
     *
     * @return true, если учетные данные не истекли
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    /**
     * Метод для проверки, активирован ли аккаунт.
     *
     * @return true, если аккаунт активен
     */
    @Override
    public boolean isEnabled() {
        return isActive;
    }

    /**
     * Метод для создания объекта UserDetailsImpl на основе объекта ApplicationUser.
     *
     * @param user объект ApplicationUser
     * @return объект UserDetailsImpl
     */
    public static UserDetails fromApplicationUser(ApplicationUser user) {
        return new User(
                user.getLogin(),
                user.getPasswordHash(),
                user.getRole().getGrantedAuthorities()
        );
    }
}
