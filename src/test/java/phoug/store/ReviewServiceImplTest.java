package phoug.store.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import phoug.store.model.Product;
import phoug.store.model.Review;
import phoug.store.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Product testProduct;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        testReview = new Review("Good product!", "Alice", 4, testProduct);
        testReview.setId(100L);
    }

    @Test
    void testSaveReview() {
        when(reviewRepository.save(testReview)).thenReturn(testReview);

        reviewService.saveReview(testReview);

        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void testFindReviewsByProduct_nonEmpty() {
        when(reviewRepository.findByProduct(testProduct)).thenReturn(Arrays.asList(testReview));

        List<Review> reviews = reviewService.findReviewsByProduct(testProduct);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals("Good product!", reviews.get(0).getComment());
    }

    @Test
    void testFindReviewsByProduct_empty() {
        when(reviewRepository.findByProduct(testProduct)).thenReturn(Collections.emptyList());

        List<Review> reviews = reviewService.findReviewsByProduct(testProduct);

        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }

    @Test
    void testFindReviewById_found() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(testReview));

        Optional<Review> result = reviewService.findReviewById(100L);

        assertTrue(result.isPresent());
        assertEquals(testReview, result.get());
    }

    @Test
    void testFindReviewById_notFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Review> result = reviewService.findReviewById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteReviewById() {
        doNothing().when(reviewRepository).deleteById(100L);

        reviewService.deleteReviewById(100L);

        verify(reviewRepository, times(1)).deleteById(100L);
    }

    @Test
    void testUpdateReview_existing() {
        Review updated = new Review("Updated comment", "Bob", 5, testProduct);
        updated.setId(100L);

        when(reviewRepository.findById(100L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        reviewService.updateReview(100L, updated);

        // Verify fields were updated
        assertEquals("Bob", testReview.getAuthor());
        assertEquals("Updated comment", testReview.getComment());
        assertEquals(5, testReview.getRating());
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void testUpdateReview_notExisting() {
        Review updated = new Review("Updated comment", "Bob", 5, testProduct);

        when(reviewRepository.findById(200L)).thenReturn(Optional.empty());

        reviewService.updateReview(200L, updated);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void testExistsById_true() {
        when(reviewRepository.existsById(100L)).thenReturn(true);

        assertTrue(reviewService.existsById(100L));
    }

    @Test
    void testExistsById_false() {
        when(reviewRepository.existsById(200L)).thenReturn(false);

        assertFalse(reviewService.existsById(200L));
    }
}
