package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.Product;
import com.mtuci.poklad.repositories.ProductRepository;
import com.mtuci.poklad.requests.DataProductRequest;
import com.mtuci.poklad.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    private Product edit(Product product, DataProductRequest request) {
        product.setName(request.getName());
        product.setBlocked(request.isBlocked());
        return product;
    }

    @Override
    public Product save(Product request) {
        Product product = new Product();
        product.setId(request.getId());
        product.setName(request.getName());
        product.setBlocked(request.isBlocked());

        return productRepository.save(product);
    }


    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product update(DataProductRequest request) {

        Product product = productRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException("Продукт не найден")
        );


        product.setName(request.getName());
        product.setBlocked(request.isBlocked());


        return productRepository.save(product);
    }


    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);  // Прямо передаем id
    }
}
