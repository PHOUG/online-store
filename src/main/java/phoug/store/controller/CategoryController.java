package phoug.store.controller;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phoug.store.model.Category;
import phoug.store.service.CategoryService;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/{categoryId}/add/{productId}")
    public ResponseEntity<String> addProductToCategory(@PathVariable Long categoryId,
                                                       @PathVariable Long productId) {
        boolean success = categoryService.addProductToCategory(categoryId, productId);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body("Product added to category!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: product or category not found, or product already added.");
        }
    }

    @DeleteMapping("/{categoryId}/remove/{productId}")
    public ResponseEntity<String> removeProductFromCategory(@PathVariable Long categoryId,
                                                            @PathVariable Long productId) {
        boolean success = categoryService.removeProductFromCategory(categoryId, productId);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body("Product removed from category!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Error: Product or category not found.");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category has been created!");
    }

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
