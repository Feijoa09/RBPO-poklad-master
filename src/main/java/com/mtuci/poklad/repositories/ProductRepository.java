package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}