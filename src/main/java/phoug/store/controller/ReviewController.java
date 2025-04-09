package phoug.store.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phoug.store.model.Product;
import phoug.store.model.Review;
import phoug.store.service.ProductService;
import phoug.store.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;

    // Создание отзыва для товара по артикулу
    @PostMapping("create/{productArticle}")
    public ResponseEntity<String> createReview(@Valid @PathVariable String productArticle,
                                               @Valid @RequestBody Review review) {
        Product product = productService.findProductByArticle(productArticle);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }

        review.setProduct(product); // Связываем отзыв с товаром
        reviewService.saveReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review has been created!");
    }

    @PostMapping("create/by-id/{id}")
    public ResponseEntity<String> createReviewById(@PathVariable Long id,
                                                   @Valid @RequestBody Review review) {
        Product product = productService.findProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
        review.setProduct(product); // Связываем отзыв с товаром
        reviewService.saveReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review has been created!");
    }

    // Получение всех отзывов для товара по артикулу
    @GetMapping("search/{productArticle}")
    public ResponseEntity<List<Review>> findReviews(@PathVariable String productArticle) {
        Optional<Product> productOpt = Optional.ofNullable(
                productService.findProductByArticle(productArticle));

        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Review> reviews = reviewService.findReviewsByProduct(productOpt.get());
        return ResponseEntity.ok(reviews);
    }

    // Получение конкретного отзыва по его ID
    @GetMapping("search/by-id/{id}")
    public ResponseEntity<Review> findReview(@PathVariable Long id) {
        Optional<Review> reviewOpt = reviewService.findReviewById(id);

        return reviewOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Обновление отзыва по ID
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateReview(@PathVariable Long id,
                                               @Valid @RequestBody Review updatedReview) {
        Optional<Review> existingReviewOpt = reviewService.findReviewById(id);

        if (existingReviewOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found!");
        }

        Review existingReview = existingReviewOpt.get();
        existingReview.setAuthor(updatedReview.getAuthor());
        existingReview.setComment(updatedReview.getComment());
        existingReview.setRating(updatedReview.getRating());

        reviewService.saveReview(existingReview); // Сохраняем обновленный отзыв
        return ResponseEntity.ok("Review has been updated!");
    }

    // Удаление отзыва по ID
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        if (!reviewService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found!");
        }

        reviewService.deleteReviewById(id);
        return ResponseEntity.ok("Review has been deleted!");
    }
}
