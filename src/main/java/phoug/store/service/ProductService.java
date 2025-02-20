package phoug.store.service;

import phoug.store.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAllProducts();
    List<Product> findProductByPriceRange(double lower, double upper);

    Product findProductByName(String name);
    Product findProductByArticle(String article);
}