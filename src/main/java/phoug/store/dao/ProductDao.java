package phoug.store.dao;

import phoug.store.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface ProductDao {
    public List<Product> findAllProducts();
    public List<Product> findProductsByPriceRange(double lower, double upper);

    public Product findProductByName(String name);
    public Product findProductByArticle(String article);
}
