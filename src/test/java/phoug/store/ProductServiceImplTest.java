package phoug.store.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.JdbcTemplate;
import phoug.store.exception.ResourceNotFoundException;
import phoug.store.model.Product;
import phoug.store.repository.ProductRepository;
import phoug.store.utils.InMemoryCache;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InMemoryCache<Long, Product> productCache;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setArticle("ART123");
        product.setPrice(100.0);
    }

    @Test
    void saveProduct_shouldSaveAndCacheProduct() {
        productService.saveProduct(product);

        verify(productRepository).save(product);
        verify(productCache).put(product.getId(), product);
    }

    @Test
    void findProductById_existingInRepo_shouldReturnAndCache() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product found = productService.findProductById(1L);

        assertEquals(product, found);
        verify(productCache).put(product.getId(), product);
    }

    @Test
    void findProductById_notFound_shouldThrow() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findProductById(1L));
    }

    @Test
    void findProductByArticle_found_shouldReturnAndCacheIfNotInCache() {
        when(productRepository.findProductByArticle("ART123")).thenReturn(Optional.of(product));
        when(productCache.get(1L)).thenReturn(null);

        Product result = productService.findProductByArticle("ART123");

        assertEquals(product, result);
        verify(productCache).put(1L, product);
    }

    @Test
    void findProductByArticle_notFound_shouldThrow() {
        when(productRepository.findProductByArticle("NOT_FOUND")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findProductByArticle("NOT_FOUND"));
    }

    @Test
    void updateProduct_existing_shouldUpdateAndCache() {
        Product updated = new Product();
        updated.setName("Updated");
        updated.setArticle("ART123");
        updated.setPrice(200.0);

        when(productRepository.findProductByArticle("ART123")).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        Product result = productService.updateProduct("ART123", updated);

        assertEquals(product, result);
        assertEquals("Updated", result.getName());
        verify(productCache).put(1L, product);
    }

    @Test
    void deleteProductByArticle_existing_shouldDeleteAndEvict() {
        when(productRepository.findProductByArticle("ART123")).thenReturn(Optional.of(product));
        product.setCategories(new ArrayList<>());

        productService.deleteProductByArticle("ART123");

        verify(productRepository).delete(product);
        verify(productCache).evict(1L);
    }

    @Test
    void deleteAllProducts_shouldClearAll() {
        productService.deleteAllProducts();

        verify(productRepository).deleteAll();
        verify(productCache).clear();
        verify(jdbcTemplate).execute("ALTER SEQUENCE products_id_seq RESTART WITH 1");
    }
}
