package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.Product;
import com.mtuci.poklad.repositories.ProductRepository;
import com.mtuci.poklad.requests.DataProductRequest;
import com.mtuci.poklad.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Добавленный импорт

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product save(Product product) {
        System.out.println("Сохранение продукта: " + product);
        if (product.getId() != null) {
            throw new IllegalArgumentException("Идентификатор не должен быть задан вручную");
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
        products.forEach(product -> Hibernate.initialize(product.getLicenses()));
        return products;
    }

    @Override
    @Transactional
    public Product update(DataProductRequest request) {
        Product product = productRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException("Продукт не найден")
        );

        System.out.println("Обновление продукта с параметрами: " + request);

        product.setName(request.getName());
        product.setBlocked(request.isBlocked());

        return productRepository.save(product);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Продукт не найден")
        );
        System.out.println("Удаление продукта: " + product);

        productRepository.delete(product);
    }


    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
}

