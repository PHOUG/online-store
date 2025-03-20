package phoug.store.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import phoug.store.model.Category;
import phoug.store.model.Product;
import phoug.store.repository.CategoryRepository;
import phoug.store.repository.ProductRepository;
import phoug.store.service.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Primary
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    // Добавить товар в категорию
    public void addProductToCategory(Long categoryId, Long productId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        Optional<Product> product = productRepository.findById(productId);

        if (category.isPresent() && product.isPresent()) {
            category.get().getProducts().add(product.get());
            categoryRepository.save(category.get()); // Сохраняем обновленную категорию
        }
    }

    // Удалить товар из категории
    public void removeProductFromCategory(Long categoryId, Long productId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        Optional<Product> product = productRepository.findById(productId);

        if (category.isPresent() && product.isPresent()) {
            category.get().getProducts().remove(product.get());
            categoryRepository.save(category.get()); // Сохраняем обновленную категорию
        }
    }

    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void updateCategory(Long id, Category updatedCategory) {
        Optional<Category> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            category.setCategoryName(updatedCategory.getCategoryName()); // Обновляем данные

            categoryRepository.save(category); // Сохраняем обновлённую категорию
        }
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }
}
