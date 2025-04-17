package phoug.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Category Controller", description = "API для управления категориями товаров")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/{categoryId}/add/{productId}")
    @Operation(summary = "Связать товар и категорию",
            description = "Связывает товар и категорию по их ID")
    @ApiResponse(responseCode = "200", description = "Пара создана")
    @ApiResponse(responseCode = "400", description = "Пара уже была создана")
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
    @Operation(summary = "Удалить связь категории и продукта",
            description = "Удаляет связь товара и категории по их ID")
    @ApiResponse(responseCode = "200", description = "Пара удалена")
    @ApiResponse(responseCode = "400", description = "Товар или категория не обнаружены")
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
    @Operation(summary = "Создать новую категорию",
            description = "Создаёт новую категорию, без каких либо связей")
    @ApiResponse(responseCode = "204", description = "Категория создана")
    public ResponseEntity<String> createCategory(@Valid @RequestBody Category category) {
        categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("Category has been created!");
    }

    @GetMapping("search/all")
    @Operation(summary = "Вывод всех категорий",
            description = "Выводит все категории и связанные с ними товары")
    @ApiResponse(responseCode = "200", description = "Категории найдены и выведены")
    @ApiResponse(responseCode = "404", description = "Категорий не найдено")
    public List<Category> findAllCategory() {
        return categoryService.findAllCategory();
    }

    @GetMapping("search/{id}")
    @Operation(summary = "Поиск категорию по ID",
            description = "Выводит категорию по переданному ID")
    @ApiResponse(responseCode = "200", description = "Категория найдена")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
    public Optional<Category> findCategoryById(@PathVariable Long id) {
        return categoryService.findCategoryById(id);
    }

    @PutMapping("update/{id}")
    @Operation(summary = "Обновить категорию по ID",
            description = "Обновляет категорию по переданному ID")
    @ApiResponse(responseCode = "200", description = "Категория успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,
                                                 @Valid @RequestBody Category updatedCategory) {
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
    @Operation(summary = "Удаление категорию по ID",
            description = "Удалить категорию по переданному ID")
    @ApiResponse(responseCode = "200", description = "Категория успешно удалена")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
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
