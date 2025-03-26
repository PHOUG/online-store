package phoug.store.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import phoug.store.exception.ResourceNotFoundException;
import phoug.store.model.Category;
import phoug.store.model.Product;
import phoug.store.repository.ProductRepository;
import phoug.store.service.ProductService;
import phoug.store.utils.InMemoryCache;

@Service
@AllArgsConstructor
@Primary
public class ProductServiceImpl implements ProductService {
    private static final String NOTFOUND = " not found";
    private final ProductRepository productRepository;
    private final InMemoryCache<Long, Product> productCache;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
        productCache.put(product.getId(), product);
    }

    @Override
    public Product findProductById(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            productCache.put(product.getId(), product);
            return product;
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findProductsByPriceRange(double lower, double upper) {
        // Ищем все товары в заданном ценовом диапазоне из базы данных
        List<Product> products = productRepository.findProductsByPriceBetween(lower, upper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Products in this price range not found"));

        // Перебираем найденные продукты и сохраняем их в кэш по их id
        for (Product product : products) {
            Product cachedProduct = productCache.get(product.getId());  // Проверяем, есть ли уже в кэше
            if (cachedProduct == null) {
                productCache.put(product.getId(), product);  // Если нет, добавляем в кэш
                logger.info("Cache put for product with id: {}", product.getId());
            }
        }

        return products;
    }


    private static final Logger logger = LoggerFactory.getLogger(InMemoryCache.class);

    @Override
    public Product findProductByArticle(String article) {
        Optional<Product> productOpt = productRepository.findProductByArticle(article);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Product cachedProduct = productCache.get(product.getId());  // Теперь ищем по id
            if (cachedProduct != null) {
                return cachedProduct;  // Если продукт найден в кэше, возвращаем его
            }
            productCache.put(product.getId(), product);
            return product;
        } else {
            throw new ResourceNotFoundException("Product with article " + article + " not found");
        }
    }


    @Override
    public Product findProductByName(String name) {
        // Сначала ищем продукт по имени в базе данных
        Optional<Product> productOpt = productRepository.findProductByName(name);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Используем id продукта для поиска в кэше
            Product cachedProduct = productCache.get(product.getId());  // Теперь ищем по id

            if (cachedProduct != null) {
                return cachedProduct;  // Если продукт найден в кэше, возвращаем его
            }

            // Если продукт не найден в кэше, сохраняем его в кэш по id
            productCache.put(product.getId(), product);
            return product;
        } else {
            throw new ResourceNotFoundException("Product with name " + name + " not found");
        }
    }



    @Override
    public Product updateProduct(String article, Product updatedProduct) {
        Product existingProduct = productRepository.findProductByArticle(article)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with article " + article + NOTFOUND));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setArticle(updatedProduct.getArticle());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImage(updatedProduct.getImage());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setSize(updatedProduct.getSize());
        existingProduct.setColor(updatedProduct.getColor());

        productCache.put(existingProduct.getId(), existingProduct);

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProductByArticle(String article) {
        Optional<Product> productOpt = productRepository.findProductByArticle(article);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            for (Category category : product.getCategories()) {
                category.getProducts().remove(product);
            }

            productRepository.delete(product);
            productCache.evict(product.getId());
        }
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAll();
        productCache.clear();
        jdbcTemplate.execute("ALTER SEQUENCE products_id_seq RESTART WITH 1");
    }

    @Override
    public List<Product> findProductsByCategories(List<String> categories) {
        return productRepository.findProductsByAllCategories(categories, categories.size());
    }

    // Метод для получения всех значений (без ключей) из кэша
    @Override
    public List<Product> getCachedProducts() {
        return (List<Product>) productCache.getAllValues();
    }
}
