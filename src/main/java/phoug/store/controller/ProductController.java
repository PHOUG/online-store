package phoug.store.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import phoug.store.model.Product;
import phoug.store.service.ProductService;


@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping()
    public List<Product> findAllProducts() {
        return service.findAllProducts();
    }

    @GetMapping("/price-range/{lower}-{upper}")
    public List<Product> findProductsByPriceRange(@PathVariable double lower,
                                                  @PathVariable double upper) {
        return service.findProductByPriceRange(lower, upper);
    }


    @GetMapping("/search")
    public Product findProductByName(@RequestParam String name) {
        return service.findProductByName(name);
    }

    @GetMapping("/{article}")
    public Product findProductByArticle(@PathVariable String article) {
        return service.findProductByArticle(article);
    }
}
