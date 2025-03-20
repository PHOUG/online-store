package phoug.store.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import phoug.store.exception.ResourceNotFoundException;
import phoug.store.model.Product;
import phoug.store.repository.ProductRepository;
import phoug.store.service.ProductService;

@Service
@AllArgsConstructor
@Primary
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findProductsByPriceRange(double lower, double upper) {
        return productRepository.findProductsByPriceBetween(lower, upper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product in this range not found"));
    }

    @Override
    public Product findProductByName(String name) {
        return productRepository.findProductByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with name " + name + " not found"));
    }

    @Override
    public Product findProductByArticle(String article) {
        // Если продукт не найден, выбрасываем исключение
        return productRepository.findProductByArticle(article)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with article " + article + " not found"));
    }

    @Override
    public Product updateProduct(String article, Product updatedProduct) {
        Product existingProduct = productRepository.findProductByArticle(article)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with article " + article + " not found"));

        // Обновляем все поля
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setArticle(updatedProduct.getArticle());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImage(updatedProduct.getImage());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setSize(updatedProduct.getSize());
        existingProduct.setColor(updatedProduct.getColor());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProductByArticle(String article) {
        Optional<Product> product = productRepository.findProductByArticle(article);
        if (product.isPresent()) {
            productRepository.delete(product.get());
        }
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAll(); // Удаляем все товары
        // Сбрасываем автоинкремент
        jdbcTemplate.execute("ALTER SEQUENCE products_id_seq RESTART WITH 1");
    }
}
