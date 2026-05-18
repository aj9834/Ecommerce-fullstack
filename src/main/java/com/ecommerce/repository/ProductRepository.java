package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	// Get all visible products (active = true)
	List<Product> findByActiveTrue();

	// Get all products in a category
	List<Product> findByCategoryAndActiveTrue(String category);

	// Search by name containing a keyword (case-insensitive)
	List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.productId = :productId")
	Optional<Product> findByIdForUpdate(@Param("productId") Long productId);
}
