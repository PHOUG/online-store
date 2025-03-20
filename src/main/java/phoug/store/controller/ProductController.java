package phoug.store.controller;

import java.util.List;
import lombok.AllArgsConstructor;
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


@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    // Create-POST создать новую карточку товара
    @PostMapping("create")
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        // Сохраняем товар в репозитории
        productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product has been created!");
    }

    // Read-GET вывод всех товаров
    @GetMapping("search/all")
    public List<Product> findAllProducts() {
        return productService.findAllProducts();
    }

    // Read-GET вывод товаров в диапазоне цен
    @GetMapping("search/price_range/{lower}-{upper}")
    public List<Product> findProductsByPriceRange(@PathVariable double lower,
                                                  @PathVariable double upper) {
        return productService.findProductsByPriceRange(lower, upper);
    }

    // Read-GET вывод товаров по имени
    @GetMapping("search/name")
    public Product findProductByName(@RequestParam String name) {
        return productService.findProductByName(name);
    }

    // Read-GET вывод товара по артиклю
    @GetMapping("search/{article}")
    public Product findProductByArticle(@PathVariable String article) {
        return productService.findProductByArticle(article);
    }

    @PutMapping("update/{article}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String article,
            @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(article, updatedProduct);
        return ResponseEntity.ok(updated);
    }

    // DELETE удаление продукта по названию
    @DeleteMapping("delete/{article}")
    public ResponseEntity<String> deleteProductByArticle(@PathVariable String article) {
        productService.deleteProductByArticle(article);
        return ResponseEntity.status(HttpStatus.OK).body("Product has been deleted!");
    }

    @DeleteMapping("delete/all")
    public void deleteAllProducts() {
        productService.deleteAllProducts();
    }
}
