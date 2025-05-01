package phoug.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import phoug.store.model.Product;
import phoug.store.service.ProductService;
import phoug.store.service.VisitCounterService;


@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller", description = "API для управления товарами")
public class ProductController {
    private final ProductService productService;
    private final VisitCounterService visitCounterService;

    public ProductController(ProductService productService,
                             VisitCounterService visitCounterService) {
        this.productService = productService;
        this.visitCounterService = visitCounterService;
    }

    // Create-POST создать новую карточку товара
    @PostMapping("create")
    @Operation(summary = "Создать новый товар",
            description = "Создаёт отзыв, связывает его с товаром с передаваемым артикулом")
    @ApiResponse(responseCode = "200", description = "Товар успешно создан")
    public ResponseEntity<String> createProduct(@Valid @RequestBody Product product) {
        // Сохраняем товар в репозитории
        productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product has been created!");
    }

    // Read-GET вывод всех товаров
    @GetMapping("search/all")
    @Operation(summary = "Вывод списка всех товаров",
            description = "Выводит список абсолютно всех товаров")
    @ApiResponse(responseCode = "200", description = "Для вывода нашлись товары, список выведен")
    @ApiResponse(responseCode = "404", description = "Не найдено ни одного товара")
    public List<Product> findAllProducts() {
        return productService.findAllProducts();
    }

    // Read-GET вывод товаров в диапазоне цен
    @GetMapping("search/price_range/{lower}-{upper}")
    @Operation(summary = "Вывод товаров в диапазоне цены",
            description = "Выводит список абсолютно всех товаров,"
                    + " цены которых лежат в рамках переданных значений")
    @ApiResponse(responseCode = "200", description = "Для вывода нашлись товары, список выведен")
    @ApiResponse(responseCode = "404", description = "Не найдено ни одного товара "
            + "вообще или в данном диапазоне")
    public List<Product> findProductsByPriceRange(@PathVariable double lower,
                                                  @PathVariable double upper) {
        return productService.findProductsByPriceRange(lower, upper);
    }

    // Read-GET вывод товаров по имени
    @Operation(summary = "Вывод товара по названию",
            description = "Выведет товар с соответсвующим названием")
    @ApiResponse(responseCode = "200", description = "Товар успешно найден")
    @ApiResponse(responseCode = "404", description = "Товара с таким названием нет")
    @GetMapping("search/name")
    public ResponseEntity<Product> findProductByName(
            @Parameter(description = "Название товара")
            @RequestParam String name) {
        // Подсчет посещений для URL
        visitCounterService.recordVisit("products/search/name?name=" + name);

        // Поиск товара
        Product product = productService.findProductByName(name);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    // Read-GET вывод товара по артиклю
    @GetMapping("search/{article}")
    @Operation(summary = "Вывод товара по уникальному артикулу",
            description = "Выводит товар с соответсвующим артикулом")
    @ApiResponse(responseCode = "200", description = "Товар успешно найден")
    @ApiResponse(responseCode = "404", description = "Товара с таким артикулом нет")
    public Product findProductByArticle(@PathVariable String article) {
        return productService.findProductByArticle(article);
    }

    @GetMapping("search/categories")
    @Operation(summary = "Выводит товары по категориям",
            description = "Выводит список всех товаров, которые принадлежат данным категориям")
    @ApiResponse(responseCode = "200", description = "Товары успешно найдены")
    @ApiResponse(responseCode = "404", description = "Товары в данных категориях не найдены")
    public ResponseEntity<List<Product>> findProductsByCategories(
            @RequestParam(name = "category") List<String> categories) {
        List<Product> products = productService.findProductsByCategories(categories);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(products);
        }
        return ResponseEntity.ok(products);
    }

    @PutMapping("update/{article}")
    @Operation(summary = "Обновляет товар по артикулу",
            description = "Обновляет товар с определённым артикулом")
    @ApiResponse(responseCode = "200", description = "Товар успешно обновлён")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String article,
            @Valid @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(article, updatedProduct);
        return ResponseEntity.ok(updated);
    }

    // DELETE удаление продукта по названию
    @DeleteMapping("delete/{article}")
    @Operation(summary = "Удаляет товар по артикулу",
            description = "Удаляет товар с определённым артикулом")
    @ApiResponse(responseCode = "200", description = "Товар успешно удалён")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public ResponseEntity<String> deleteProductByArticle(@PathVariable String article) {
        productService.deleteProductByArticle(article);
        return ResponseEntity.status(HttpStatus.OK).body("Product has been deleted!");
    }

    // Эндпоинт для получения всех товаров из кэша
    @GetMapping("/cached")
    @Operation(summary = "Показывае товары в кэше",
            description = "Выводит все товары, которые в данный момент находятся в кэше")
    @ApiResponse(responseCode = "200", description = "Товары найдены")
    @ApiResponse(responseCode = "404", description = "Товары не найден")
    public ResponseEntity<List<Product>> getCachedProducts() {
        List<Product> cachedProducts = productService.getCachedProducts();
        if (cachedProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cachedProducts);
    }
}
