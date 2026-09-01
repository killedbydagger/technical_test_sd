package com.temp.demo.repository;

import com.temp.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE CONCAT('%', :name, '%') AND p.deleted = FALSE AND p.price BETWEEN :min AND :max")
    Page<Product> findProductPaging(@Param("name") String name,
                                    @Param("min") BigDecimal minPrice,
                                    @Param("max") BigDecimal maxPrice,
                                    Pageable pageable);
}
