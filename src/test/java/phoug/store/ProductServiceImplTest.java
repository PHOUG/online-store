package phoug.store.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import phoug.store.exception.ResourceNotFoundException;
import phoug.store.model.Product;
import phoug.store.repository.ProductRepository;
import phoug.store.utils.InMemoryCache;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InMemoryCache<Long, Product> productCache;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product p1;
    private Product p2;

    @BeforeEach
    void setUp() {
        p1 = new Product();
        p1.setId(1L);
        p1.setName("Product1");
        p1.setArticle("A1");
        p1.setPrice(50.0);

        p2 = new Product();
        p2.setId(2L);
        p2.setName("Product2");
        p2.setArticle("A2");
        p2.setPrice(150.0);
    }

    @Test
    void saveProduct_shouldSaveAndCache() {
        productService.saveProduct(p1);
        verify(productRepository).save(p1);
        verify(productCache).put(1L, p1);
    }

    @Test
    void findProductById_notFound_shouldThrow() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.findProductById(1L));
    }

    @Test
    void findAllProducts_cacheEmpty_repoHasProducts() {
        when(productCache.getAllValues()).thenReturn(List.of());
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));
        when(productCache.get(1L)).thenReturn(null);
        when(productCache.get(2L)).thenReturn(null);

        List<Product> result = productService.findAllProducts();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(p1, p2)));
    }

    @Test
    void findAllProducts_cacheHasOne_repoHasBoth() {
        when(productCache.getAllValues()).thenReturn(List.of(p1));
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));
        when(productCache.get(1L)).thenReturn(p1);
        when(productCache.get(2L)).thenReturn(null);

        List<Product> result = productService.findAllProducts();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(p1, p2)));
    }

    @Test
    void findProductsByPriceRange_cacheEmpty_newProductsCachedAndReturned() {
        when(productCache.getAllValues()).thenReturn(List.of());
        when(productRepository.findProductsByPriceBetween(0.0, 100.0)).thenReturn(Optional.of(List.of(p1)));

        List<Product> result = productService.findProductsByPriceRange(0.0, 100.0);

        assertEquals(1, result.size());
        assertEquals(p1, result.get(0));
        verify(productCache).put(1L, p1);
    }

    @Test
    void findProductsByPriceRange_cacheHasMatch_repoHasOthers() {
        when(productCache.getAllValues()).thenReturn(List.of(p1));
        when(productRepository.findProductsByPriceBetween(0.0, 200.0)).thenReturn(Optional.of(List.of(p1, p2)));
        when(productCache.get(1L)).thenReturn(p1);
        when(productCache.get(2L)).thenReturn(null);

        List<Product> result = productService.findProductsByPriceRange(0.0, 200.0);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(p1, p2)));
        verify(productCache).put(2L, p2);
    }

    @Test
    void findProductsByPriceRange_noProducts_shouldThrow() {
        when(productCache.getAllValues()).thenReturn(List.of());
        when(productRepository.findProductsByPriceBetween(0, 10)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.findProductsByPriceRange(0, 10));
    }

    @Test
    void findProductByArticle_notInCache_shouldCacheAndReturn() {
        when(productRepository.findProductByArticle("A1")).thenReturn(Optional.of(p1));
        when(productCache.get(1L)).thenReturn(null);

        Product result = productService.findProductByArticle("A1");

        assertEquals(p1, result);
        verify(productCache).put(1L, p1);
    }

    @Test
    void findProductByArticle_inCache_shouldReturnCached() {
        when(productRepository.findProductByArticle("A1")).thenReturn(Optional.of(p1));
        when(productCache.get(1L)).thenReturn(p2);

        Product result = productService.findProductByArticle("A1");

        assertEquals(p2, result);
        verify(productCache, never()).put(anyLong(), any());
    }

    @Test
    void findProductByArticle_notFound_shouldThrow() {
        when(productRepository.findProductByArticle("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.findProductByArticle("X"));
    }

    @Test
    void findProductByName_notInCache_shouldCacheAndReturn() {
        when(productRepository.findProductByName("Product1")).thenReturn(Optional.of(p1));
        when(productCache.get(1L)).thenReturn(null);

        Product result = productService.findProductByName("Product1");

        assertEquals(p1, result);
        verify(productCache).put(1L, p1);
    }

    @Test
    void findProductByName_inCache_shouldReturnCached() {
        when(productRepository.findProductByName("Product1")).thenReturn(Optional.of(p1));
        when(productCache.get(1L)).thenReturn(p2);

        Product result = productService.findProductByName("Product1");

        assertEquals(p2, result);
        verify(productCache, never()).put(anyLong(), any());
    }

    @Test
    void findProductByName_notFound_shouldThrow() {
        when(productRepository.findProductByName("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.findProductByName("X"));
    }

    @Test
    void updateProduct_existing_shouldUpdatePropertiesAndCache() {
        Product updated = new Product();
        updated.setName("New");
        updated.setArticle("A1");
        updated.setPrice(300.0);
        updated.setDescription("Desc");
        updated.setImage("Img");
        updated.setBrand("Brand");
        updated.setSize("M");
        updated.setColor("Red");

        when(productRepository.findProductByArticle("A1")).thenReturn(Optional.of(p1));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct("A1", updated);

        assertEquals("New", result.getName());
        assertEquals(300.0, result.getPrice());
        verify(productCache).put(1L, p1);
    }

    @Test
    void updateProduct_notFound_shouldThrow() {
        when(productRepository.findProductByArticle("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct("X", p1));
    }

    @Test
    void deleteProductByArticle_existing_shouldDeleteAndEvict() {
        when(productRepository.findProductByArticle("A1")).thenReturn(Optional.of(p1));
        p1.setCategories(List.of());

        productService.deleteProductByArticle("A1");

        verify(productRepository).delete(p1);
        verify(productCache).evict(1L);
    }

    @Test
    void deleteProductByArticle_notFound_shouldDoNothing() {
        when(productRepository.findProductByArticle("X")).thenReturn(Optional.empty());

        productService.deleteProductByArticle("X");

        verify(productRepository, never()).delete(any());
        verify(productCache, never()).evict(anyLong());
    }

    @Test
    void findProductsByCategories_shouldDelegateToRepository() {
        List<Product> list = List.of(p1, p2);
        when(productRepository.findProductsByAllCategories(List.of("C1"), 1)).thenReturn(list);

        List<Product> result = productService.findProductsByCategories(List.of("C1"));

        assertEquals(list, result);
    }

    @Test
    void getCachedProducts_shouldReturnCacheValues() {
        List<Product> list = List.of(p1);
        when(productCache.getAllValues()).thenReturn(list);

        List<Product> result = productService.getCachedProducts();

        assertEquals(list, result);
    }
}
