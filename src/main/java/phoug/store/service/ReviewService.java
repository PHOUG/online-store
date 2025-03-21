package phoug.store.service;

import java.util.List;
import java.util.Optional;
import phoug.store.model.Product;
import phoug.store.model.Review;

public interface ReviewService {
    // Сохранение отзыва
    void saveReview(Review review);

    // Поиск всех отзывов для конкретного товара
    List<Review> findReviewsByProduct(Product product);

    // Поиск отзыва по ID
    Optional<Review> findReviewById(Long id);

    // Удаление отзыва по ID
    void deleteReviewById(Long id);

    void updateReview(Long id, Review updatedReview);

    // Проверка, существует ли отзыв
    boolean existsById(Long id);
}
