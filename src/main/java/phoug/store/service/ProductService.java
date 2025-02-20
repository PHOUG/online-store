package phoug.store.service;

import java.util.List;
import phoug.store.model.Product;

public interface ProductService {
    List<Product> findAllProducts();

    List<Product> findProductByPriceRange(double lower, double upper);

    Product findProductByName(String name);

    Product findProductByArticle(String article);
}