package phoug.store.service.impl;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
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
@Primary
public class ProductServiceImpl implements ProductService {
    private static final String NOTFOUND = " not found";
    private final ProductRepository productRepository;
    private final InMemoryCache<Long, Product> productCache;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository,
                              InMemoryCache<Long, Product> productCache,
                              JdbcTemplate jdbcTemplate) {
        this.productRepository = productRepository;
        this.productCache = productCache;
        this.jdbcTemplate = jdbcTemplate;
    }

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
        // Получаем все товары из кэша
        List<Product> cachedProducts = new ArrayList<>(productCache.getAllValues());
        logger.info("Loaded {} products from cache", cachedProducts.size());

        // Получаем из БД только те товары, которых нет в кэше
        List<Product> dbProducts = productRepository.findAll().stream()
                .filter(product -> productCache.get(product.getId()) == null)
                .toList();

        logger.info("Loaded {} products from database", dbProducts.size());

        // Объединяем данные из кэша и БД
        cachedProducts.addAll(dbProducts);
        return cachedProducts;
    }



    @Override
    public List<Product> findProductsByPriceRange(double lower, double upper) {
        // Ищем товары в кэше
        List<Product> cachedProducts = productCache.getAllValues().stream()
                .filter(product -> product.getPrice() >= lower && product.getPrice() <= upper)
                .toList();

        logger.info("Cache contains {} products in price range: {} - {}",
                cachedProducts.size(), lower, upper);

        // Ищем товары в БД, которые еще не находятся в кэше
        List<Product> dbProducts = productRepository.findProductsByPriceBetween(lower, upper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Products in this price range not found"));

        // Фильтруем только новые товары, которых нет в кэше
        List<Product> newProducts = dbProducts.stream()
                .filter(product -> productCache.get(product.getId()) == null)
                .toList();

        // Добавляем в кэш новые товары
        for (Product product : newProducts) {
            productCache.put(product.getId(), product);
        }

        if (!newProducts.isEmpty()) {
            logger.info("Products moved to cache in price range: {} - {}", lower, upper);
        }

        // Возвращаем объединенный список (из кэша + новые из БД)
        List<Product> result = new ArrayList<>(cachedProducts);
        result.addAll(newProducts);

        return result;
    }

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
            throw new ResourceNotFoundException("Product with article " + article + NOTFOUND);
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
            throw new ResourceNotFoundException("Product with name " + name + NOTFOUND);
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
