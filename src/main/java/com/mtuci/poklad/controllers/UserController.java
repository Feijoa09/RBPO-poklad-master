package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.requests.DataUserRequest;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settings/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserServiceImpl userService;

    /**
     * Создание нового пользователя.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @param email    email пользователя
     * @param role     роль пользователя
     * @return ответ с данными созданного пользователя или ошибкой
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam String login,
                                  @RequestParam String password,
                                  @RequestParam String email,
                                  @RequestParam String role) {
        try {
            // Создаем новый объект DataUserRequest, заполняя его значениями из параметров запроса
            DataUserRequest request = new DataUserRequest(null, login, password, email, role);
            ApplicationUser user = userService.save(request);
            request.setId(user.getId());
            return ResponseEntity.status(201).body(request); // Статус 201 для успешного создания
        } catch (Exception e) {
            return handleError("Ошибка при создании пользователя", e);
        }
    }

    /**
     * Получение всех пользователей.
     *
     * @return список всех пользователей
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<ApplicationUser> users = userService.getAll();
            List<DataUserRequest> data = users.stream()
                    .map(user -> new DataUserRequest(
                            user.getId(),
                            user.getLogin(),
                            user.getPasswordHash(),
                            user.getEmail(),
                            user.getRole().name()
                    ))
                    .toList();
            return ResponseEntity.ok(data); // Статус 200 для успешного запроса
        } catch (Exception e) {
            return handleError("Ошибка при получении пользователей", e);
        }
    }

    /**
     * Обновление данных пользователя.
     *
     * @param id       идентификатор пользователя
     * @param login    новый логин пользователя
     * @param password новый пароль пользователя
     * @param email    новый email пользователя
     * @param role     новая роль пользователя
     * @return ответ с обновленными данными пользователя
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam Long id,
                                    @RequestParam String login,
                                    @RequestParam String password,
                                    @RequestParam String email,
                                    @RequestParam String role) {
        try {
            // Создаем новый объект DataUserRequest с данными для обновления
            DataUserRequest request = new DataUserRequest(id, login, password, email, role);
            userService.update(request);
            return ResponseEntity.ok(request); // Статус 200 для успешного обновления
        } catch (Exception e) {
            return handleError("Ошибка при обновлении пользователя", e);
        }
    }

    /**
     * Удаление пользователя.
     *
     * @param id идентификатор пользователя для удаления
     * @return ответ с сообщением о успешном удалении
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("Пользователь удалён"); // Статус 200 для успешного удаления
        } catch (Exception e) {
            return handleError("Ошибка при удалении пользователя", e);
        }
    }

    /**
     * Универсальный метод для обработки ошибок.
     *
     * @param message сообщение об ошибке
     * @param e       исключение
     * @return ответ с сообщением об ошибке
     */
    private ResponseEntity<?> handleError(String message, Exception e) {
        return ResponseEntity.status(400).body(message + ": " + e.getMessage()); // Статус 400 для ошибки
    }
}
