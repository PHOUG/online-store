package phoug.store.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import phoug.store.model.Product;
import phoug.store.model.Review;
import phoug.store.repository.ReviewRepository;
import phoug.store.service.ReviewService;

@Service
@Primary
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    @Override
    public List<Review> findReviewsByProduct(Product product) {
        return reviewRepository.findByProduct(product);
    }

    @Override
    public Optional<Review> findReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public void updateReview(Long id, Review updatedReview) {
        Optional<Review> existingReviewOpt = reviewRepository.findById(id);

        if (existingReviewOpt.isPresent()) {
            Review existingReview = existingReviewOpt.get();
            existingReview.setAuthor(updatedReview.getAuthor());
            existingReview.setComment(updatedReview.getComment());
            existingReview.setRating(updatedReview.getRating());

            reviewRepository.save(existingReview);
        }
    }


    @Override
    public boolean existsById(Long id) {
        return reviewRepository.existsById(id);
    }
}
