package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.Product;
import com.mtuci.poklad.requests.DataProductRequest;
import com.mtuci.poklad.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settings/product")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {

    private final ProductServiceImpl productService;

    /**
     * Сохранение нового продукта.
     *
     * @param name     название продукта
     * @param isBlocked статус блокировки
     * @return ответ с данными созданного продукта
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam String name,
                                  @RequestParam boolean isBlocked) {
        try {
            // Проверка на пустое значение name
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body("Поле 'name' не может быть пустым");
            }

            // Сохраняем продукт через сервис
            Product product = productService.save(new Product());

            // Возвращаем ответ с кодом 201
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            return handleError("Ошибка при сохранении продукта", e);
        }
    }

    /**
     * Получение всех продуктов.
     *
     * @return список всех продуктов
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Product> products = productService.getAll();

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return handleError("Ошибка при получении списка продуктов", e);
        }
    }

    /**
     * Обновление данных продукта.
     *
     * @param id         идентификатор продукта
     * @param name       новое название продукта
     * @param isBlocked  новый статус блокировки
     * @return ответ с обновленными данными продукта
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam Long id,
                                    @RequestParam String name,
                                    @RequestParam boolean isBlocked) {
        try {
            // Проверка на пустое значение name
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body("Поле 'name' не может быть пустым");
            }

            // Создаем объект DataProductRequest с данными для обновления
            DataProductRequest request = new DataProductRequest();
            request.setId(id);
            request.setName(name);
            request.setBlocked(isBlocked);

            // Обновляем продукт через сервис
            productService.update(request);

            // Возвращаем успешный ответ
            return ResponseEntity.ok("Продукт успешно обновлен");

        } catch (Exception e) {
            return handleError("Ошибка при обновлении продукта", e);
        }
    }

    /**
     * Удаление продукта.
     *
     * @param id идентификатор продукта для удаления
     * @return ответ с сообщением о успешном удалении
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            productService.delete(id);
            return ResponseEntity.ok("Продукт удалён");
        } catch (Exception e) {
            return handleError("Ошибка при удалении продукта", e);
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message + ": " + e.getMessage());
    }
}
