package phoug.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phoug.store.model.Category;
import phoug.store.service.CategoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // Добавить товар в категорию
    @PostMapping("/{categoryId}/add/{productId}")
    public ResponseEntity<String> addProductToCategory(@PathVariable Long categoryId, @PathVariable Long productId) {
        categoryService.addProductToCategory(categoryId, productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product added to category!");
    }

    // Удалить товар из категории
    @DeleteMapping("/{categoryId}/remove/{productId}")
    public ResponseEntity<String> removeProductFromCategory(@PathVariable Long categoryId, @PathVariable Long productId) {
        categoryService.removeProductFromCategory(categoryId, productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product removed from category!");
    }

    // Create-POST создать новую карточку товара
    @PostMapping("create")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        // Сохраняем товар в репозитории
        categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category has been created!");
    }

    // Read-GET вывод всех товаров
    @GetMapping("search/all")
    public List<Category> findAllCategory() {
        return categoryService.findAllCategory();
    }

    @GetMapping("search/{id}")
    public Optional<Category> findCategoryById(@PathVariable Long id) {
        return categoryService.findCategoryById(id);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,
                                                 @RequestBody Category updatedCategory) {
        Optional<Category> existingCategory = categoryService.findCategoryById(id);
        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            category.setCategoryName(updatedCategory.getCategoryName());

            categoryService.saveCategory(category); // Сохраняем обновлённую категорию
            return ResponseEntity.ok("Category updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found!");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        Optional<Category> existingCategory = categoryService.findCategoryById(id);

        if (existingCategory.isPresent()) {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok("Category deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found!");
        }
    }

}
