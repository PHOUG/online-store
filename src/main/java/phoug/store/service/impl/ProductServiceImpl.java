package phoug.store.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import phoug.store.dao.ProductDao;
import phoug.store.model.Product;
import phoug.store.service.ProductService;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductDao productRepository;

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAllProducts();
    }

    @Override
    public List<Product> findProductByPriceRange(double lower, double upper) {
        return productRepository.findProductsByPriceRange(lower, upper);
    }

    @Override
    public Product findProductByName(String name) {
        return productRepository.findProductByName(name);
    }

    @Override
    public Product findProductByArticle(String article) {
        return productRepository.findProductByArticle(article);
    }
}
