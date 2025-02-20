package phoug.store.dao.impl;

import org.springframework.stereotype.Repository;
import phoug.store.dao.ProductDao;
import phoug.store.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductDaoImpl implements ProductDao {
    private final List<Product> productArrayList = new ArrayList<>();

    // (Конструктор) Изначальная загрузка списка товаров
    public ProductDaoImpl() {
        Product prod1 = new Product("Джинсы Mademan",
                "RTLADQ051101", null,
                9600, 89.60);
        Product prod2 = new Product("Футболка Hugo",
                "RTLADQ100701", null,
                3450, 239);
        Product prod3 = new Product("Очки Palaroid",
                "RTLABI743501", null,
                6300, 349);
        Product prod4 = new Product("Кроссовки Adidas",
                "RTLADO561701", null,
                17000, 248.40);
        Product prod5 = new Product("Джемпер Mango",
                "RTLADV969101", null,
                5200, 200);
        productArrayList.add(prod1);
        productArrayList.add(prod2);
        productArrayList.add(prod3);
        productArrayList.add(prod4);
        productArrayList.add(prod5);
    }

    @Override
    public List<Product> findAllProducts() {
        return productArrayList;
    }

    @Override
    public List<Product> findProductsByPriceRange(double lower, double upper) {
        return productArrayList.stream()
                .filter(product -> product.getPrice() >= lower && product.getPrice() <= upper)
                .collect(Collectors.toList());
    }

    @Override
    public Product findProductByName(String name) {
        return productArrayList.stream()
                .filter(element -> element.getName().equals(name))
                .findFirst().orElse(null);
    }

    @Override
    public Product findProductByArticle(String article) {
        return productArrayList.stream()
                .filter(element -> element.getArticle().equals(article))
                .findFirst().orElse(null);
    }
}
