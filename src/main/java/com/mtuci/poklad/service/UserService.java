package com.mtuci.poklad.service;


import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.requests.DataUserRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<ApplicationUser> getUserById(Long id);
    Optional<ApplicationUser> getUserByLogin(String login);

    // сохранение
    ApplicationUser save(DataUserRequest request);

    // получение всех
    List<ApplicationUser> getAll();

    // обновление
    ApplicationUser update(DataUserRequest request);

    // удаление
    void delete(Long id);
}
