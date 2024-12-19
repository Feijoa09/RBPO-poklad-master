package com.mtuci.poklad.service;


import com.mtuci.poklad.models.Product;
import com.mtuci.poklad.requests.DataProductRequest;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> getProductById(Long id);

    // сохранение
    Product save(DataProductRequest request);

    // получение всех
    List<Product> getAll();

    // обновление
    Product update(DataProductRequest request);
    // удаление
    void delete(Long id);
}
