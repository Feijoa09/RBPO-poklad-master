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
@RequestMapping("/manage/product")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {

    private final ProductServiceImpl productService;

    /**
     * Сохранение нового продукта.
     *
     * @param name    название продукта
     * @param blocked статус блокировки
     * @return ответ с данными созданного продукта
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestParam Long id, @RequestParam String name, @RequestParam boolean blocked) {
        try {
            // Создаем объект DataProductRequest вручную
            DataProductRequest request = new DataProductRequest();
            request.setId(id); // Передаем ID вручную
            request.setName(name);
            request.setBlocked(blocked);

            Product product = productService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
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
            List<DataProductRequest> data = products.stream()
                    .map(product -> new DataProductRequest(
                            product.getId(),
                            product.getName(),
                            product.isBlocked()
                    ))
                    .toList();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return handleError("Ошибка при получении списка продуктов", e);
        }
    }

    /**
     * Обновление данных продукта.
     *
     * @param id      идентификатор продукта
     * @param name    новое название продукта
     * @param blocked новый статус блокировки
     * @return ответ с обновленными данными продукта
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam Long id, @RequestParam String name, @RequestParam boolean blocked) {
        try {
            // Создаем объект DataProductRequest вручную
            DataProductRequest request = new DataProductRequest();
            request.setId(id);
            request.setName(name);
            request.setBlocked(blocked);

            productService.update(request);
            return ResponseEntity.ok(request);
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
