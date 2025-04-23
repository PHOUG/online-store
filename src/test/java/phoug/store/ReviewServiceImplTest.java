package phoug.store.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import phoug.store.model.Product;
import phoug.store.model.Review;
import phoug.store.repository.ReviewRepository;
import phoug.store.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;  // Ваш репозиторий для работы с отзывами

    @Mock
    private ProductService productService;  // Сервис для работы с продуктами

    @InjectMocks
    private ReviewServiceImpl reviewService;  // Реализация ReviewService

    private Product testProduct;
    private Review testReview;

    @BeforeEach
    public void setUp() {
        // Создаем объект товара для тестов
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        // Создаем объект отзыва для тестов
        testReview = new Review("Good product!", "Alice", 4, testProduct);
    }

    @Test
    public void testSaveReview() {
        when(reviewRepository.save(testReview)).thenReturn(testReview);

        reviewService.saveReview(testReview);

        verify(reviewRepository, times(1)).save(testReview);
    }


    @Test
    public void testFindReviewsByProduct() {
        // Тестируем поиск всех отзывов по продукту
        when(reviewRepository.findByProduct(testProduct)).thenReturn(Arrays.asList(testReview));

        List<Review> reviews = reviewService.findReviewsByProduct(testProduct);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals("Good product!", reviews.get(0).getComment());
    }

    @Test
    public void testFindReviewById() {
        // Тестируем поиск отзыва по ID
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        Optional<Review> review = reviewService.findReviewById(1L);

        assertTrue(review.isPresent());
        assertEquals("Good product!", review.get().getComment());
    }

    @Test
    public void testDeleteReviewById() {
        // Тестируем удаление отзыва по ID
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReviewById(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testExistsById() {
        // Тестируем проверку существования отзыва по ID
        when(reviewRepository.existsById(1L)).thenReturn(true);

        boolean exists = reviewService.existsById(1L);

        assertTrue(exists);
    }
}
