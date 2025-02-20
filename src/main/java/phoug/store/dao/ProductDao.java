package phoug.store.dao;

import java.util.List;
import phoug.store.model.Product;

public interface ProductDao {
    public List<Product> findAllProducts();

    public List<Product> findProductsByPriceRange(double lower, double upper);

    public Product findProductByName(String name);

    public Product findProductByArticle(String article);
}
