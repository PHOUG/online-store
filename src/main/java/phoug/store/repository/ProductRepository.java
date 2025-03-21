package phoug.store.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phoug.store.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<List<Product>> findProductsByPriceBetween(double lower, double upper);

    Optional<Product> findProductByName(String name);

    Optional<Product> findProductByArticle(String article);

    Optional<Product> findProductById(Long id);
}
