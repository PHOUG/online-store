package phoug.store.service;

import java.util.List;
import org.springframework.stereotype.Service;
import phoug.store.model.Product;

public interface ProductService {
    // Creat-POST метод для добавления нового товара
    void saveProduct(Product product);

    // Read-GET для получение списка всего списка
    List<Product> findAllProducts();

    // Read-GET для поиска товаров в диапазоне цен
    List<Product> findProductsByPriceRange(double lower, double upper);

    // Read-GET для поиска товара по названию
    Product findProductByName(String name);

    // Read-GET для поиска товара по артикулу
    Product findProductByArticle(String article);

    // Update-PUT обновление товара
    Product updateProduct(String article, Product updatedProduct);

    // DELETE удаление продукта по артикулу
    void deleteProductByArticle(String article);

    void deleteAllProducts();

    Product findProductById(Long id);
}