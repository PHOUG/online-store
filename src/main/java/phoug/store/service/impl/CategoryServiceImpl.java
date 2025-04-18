package phoug.store.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import phoug.store.model.Category;
import phoug.store.model.Product;
import phoug.store.repository.CategoryRepository;
import phoug.store.repository.ProductRepository;
import phoug.store.service.CategoryService;

@Service
@AllArgsConstructor
@Primary
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    // Добавить товар в категорию
    @Transactional
    public boolean addProductToCategory(Long categoryId, Long productId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (categoryOpt.isEmpty()) {
            return false;
        }

        if (productOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();
        Product product = productOpt.get();

        if (category.getProducts().contains(product)) {
            return false;
        }

        category.getProducts().add(product);
        product.getCategories().add(category);

        categoryRepository.save(category);
        productRepository.save(product);

        return true;
    }

    // Удалить товар из категории
    @Transactional
    public boolean removeProductFromCategory(Long categoryId, Long productId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (categoryOpt.isEmpty()) {
            return false;
        }

        if (productOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();
        Product product = productOpt.get();

        if (!category.getProducts().contains(product)) {
            return false;
        }

        category.getProducts().remove(product);
        categoryRepository.save(category);

        return true;
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

    @Override
    public List<Category> saveAll(List<Category> categories) {
        // Пример простой фильтрации или преобразования, если нужно
        List<Category> validCategories = categories.stream()
                .filter(c -> c.getCategoryName() != null && c.getCategoryName().length() >= 3)
                .collect(Collectors.toList());

        return categoryRepository.saveAll(validCategories);
    }
}
