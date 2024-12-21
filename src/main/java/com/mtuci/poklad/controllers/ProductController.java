package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.Product;
import com.mtuci.poklad.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/settings/product")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {

    private final ProductServiceImpl productService;

    @PostMapping
    public ResponseEntity<?> save(@RequestParam String name,
                                  @RequestParam boolean isBlocked) {
        System.out.println("Параметры запроса: name=" + name + ", isBlocked=" + isBlocked);
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Поле 'name' не может быть пустым");
        }

        try {
            Product product = new Product();
            product.setName(name);
            product.setBlocked(isBlocked);

            Product savedProduct = productService.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return handleError("Ошибка при сохранении продукта", e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        System.out.println("Получение всех продуктов");
        try {
            List<Product> products = productService.getAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return handleError("Ошибка при получении списка продуктов", e);
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestParam Long id,
                                    @RequestParam String name,
                                    @RequestParam boolean isBlocked) {
        System.out.println("Параметры запроса: id=" + id + ", name=" + name + ", isBlocked=" + isBlocked);
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Поле 'name' не может быть пустым");
        }

        try {
            Optional<Product> optionalProduct = productService.getProductById(id);
            if (!optionalProduct.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Продукт с id " + id + " не найден");
            }

            Product product = optionalProduct.get();
            product.setName(name);
            product.setBlocked(isBlocked);

            Product updatedProduct = productService.save(product);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return handleError("Ошибка при обновлении продукта", e);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Long id) {
        System.out.println("Параметры запроса: id=" + id);
        try {
            productService.delete(id);
            return ResponseEntity.ok("Продукт удалён");
        } catch (Exception e) {
            return handleError("Ошибка при удалении продукта", e);
        }
    }

    private ResponseEntity<?> handleError(String message, Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message + ": " + e.getMessage());
    }
}
