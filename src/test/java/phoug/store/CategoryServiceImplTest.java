package phoug.store.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import phoug.store.model.Category;
import phoug.store.model.Product;
import phoug.store.repository.CategoryRepository;
import phoug.store.repository.ProductRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private Product product;

    @BeforeEach
    void setup() {
        category = new Category("Test Category");
        category.setId(1L);
        product = new Product();
        product.setId(1L);
    }

    @Test
    void testAddProductToCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean result = categoryService.addProductToCategory(1L, 1L);

        assertTrue(result);
        assertTrue(category.getProducts().contains(product));
        verify(categoryRepository).save(category);
        verify(productRepository).save(product);
    }

    @Test
    void testAddProductToCategory_AlreadyExists() {
        category.getProducts().add(product);
        product.getCategories().add(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean result = categoryService.addProductToCategory(1L, 1L);

        assertFalse(result);
    }

    @Test
    void testAddProductToCategory_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = categoryService.addProductToCategory(1L, 1L);

        assertFalse(result);
    }

    @Test
    void testAddProductToCategory_ProductNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = categoryService.addProductToCategory(1L, 1L);

        assertFalse(result);
    }

    @Test
    void testRemoveProductFromCategory_Success() {
        category.getProducts().add(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean result = categoryService.removeProductFromCategory(1L, 1L);

        assertTrue(result);
        assertFalse(category.getProducts().contains(product));
        verify(categoryRepository).save(category);
    }

    @Test
    void testRemoveProductFromCategory_NotAssociated() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean result = categoryService.removeProductFromCategory(1L, 1L);

        assertFalse(result);
    }

    @Test
    void testRemoveProductFromCategory_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = categoryService.removeProductFromCategory(1L, 1L);

        assertFalse(result);
    }

    @Test
    void testSaveCategory() {
        categoryService.saveCategory(category);
        verify(categoryRepository).save(category);
    }

    @Test
    void testFindAllCategory() {
        List<Category> categories = List.of(category);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAllCategory();

        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getCategoryName());
    }

    @Test
    void testFindCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.findCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Category", result.get().getCategoryName());
    }

    @Test
    void testUpdateCategory_Exists() {
        Category updated = new Category("Updated");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.updateCategory(1L, updated);

        assertEquals("Updated", category.getCategoryName());
        verify(categoryRepository).save(category);
    }

    @Test
    void testUpdateCategory_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        categoryService.updateCategory(1L, new Category("Updated"));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void testDeleteCategoryById() {
        categoryService.deleteCategoryById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testSaveAll_FilterValidOnly() {
        Category valid = new Category("Valid");
        Category invalid = new Category("X");

        List<Category> input = List.of(valid, invalid);
        when(categoryRepository.saveAll(List.of(valid))).thenReturn(List.of(valid));

        List<Category> result = categoryService.saveAll(input);

        assertEquals(1, result.size());
        assertEquals("Valid", result.get(0).getCategoryName());
    }
}

