package org.example.createapi.repository;

import org.example.createapi.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByUserId(String userId);

    List<Product> findByUserIdAndType(String userId, String type);
}
