package com.ecommerce.config;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ReviewDataInitializer implements ApplicationRunner {
	private static final List<String> REVIEWER_NAMES =
			List.of("Aarav Sharma", "Meera Kapoor", "Rohan Verma");
	private static final List<String> REVIEWER_EMAILS =
			List.of("aarav.reviews@mercato.demo", "meera.reviews@mercato.demo", "rohan.reviews@mercato.demo");

	private final ProductRepository productRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public ReviewDataInitializer(
			ProductRepository productRepository,
			ReviewRepository reviewRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		this.productRepository = productRepository;
		this.reviewRepository = reviewRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		List<User> reviewers = createReviewers();
		for (Product product : productRepository.findByActiveTrue()) {
			if (reviewRepository.existsByProduct(product)) {
				continue;
			}

			createReview(
					reviewers.get(0),
					product,
					5,
					"The " + product.getName() + " feels even better than expected. Quality is excellent and delivery was neatly packed.",
					product.getImageUrl()
			);
			createReview(
					reviewers.get(1),
					product,
					4,
					"Good value for the price. I have been using it regularly and it has been reliable so far.",
					null
			);
			createReview(
					reviewers.get(2),
					product,
					product.getProductId() % 2 == 0 ? 5 : 4,
					"Looks just like the product photos and the finish is impressive. I would happily recommend it.",
					null
			);
		}
	}

	private List<User> createReviewers() {
		return java.util.stream.IntStream.range(0, REVIEWER_EMAILS.size())
				.mapToObj(index -> userRepository.findByEmail(REVIEWER_EMAILS.get(index))
						.orElseGet(() -> {
							User user = new User();
							user.setName(REVIEWER_NAMES.get(index));
							user.setEmail(REVIEWER_EMAILS.get(index));
							user.setPassword(passwordEncoder.encode("DemoReviewUser!2026"));
							user.setRole("USER");
							return userRepository.save(user);
						}))
				.toList();
	}

	private void createReview(User user, Product product, int rating, String comment, String imageUrl) {
		Review review = new Review();
		review.setUser(user);
		review.setProduct(product);
		review.setRating(rating);
		review.setComment(comment);
		review.setImageUrl(imageUrl);
		reviewRepository.save(review);
	}
}
