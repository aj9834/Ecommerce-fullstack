package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByProductOrderByUpdatedAtDesc(Product product);

	Optional<Review> findByUserAndProduct(User user, Product product);

	boolean existsByProduct(Product product);

	long countByProduct(Product product);

	@Query("select avg(r.rating) from Review r where r.product = :product")
	Double findAverageRatingByProduct(@Param("product") Product product);
}
