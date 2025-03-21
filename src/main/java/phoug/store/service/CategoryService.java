package phoug.store.service;

import phoug.store.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    boolean addProductToCategory(Long categoryId, Long productId);

    boolean removeProductFromCategory(Long categoryId, Long productId);

    void saveCategory(Category category);

    List<Category> findAllCategory();

    Optional<Category> findCategoryById(Long id);

    void updateCategory(Long id, Category updatedCategory);

    void deleteCategoryById(Long id);
}
