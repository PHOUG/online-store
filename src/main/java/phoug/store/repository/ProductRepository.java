package phoug.store.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import phoug.store.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<List<Product>> findProductsByPriceBetween(double lower, double upper);

    Optional<Product> findProductByName(String name);

    Optional<Product> findProductByArticle(String article);

    Optional<Product> findProductById(Long id);

    @Query("""
    SELECT p 
    FROM Product p 
    JOIN p.categories c 
    WHERE c.categoryName 
    IN :categories 
    GROUP BY p.id 
    HAVING COUNT(DISTINCT c.id) = :size
        """)
    List<Product> findProductsByAllCategories(
            @Param("categories") List<String> categories, @Param("size") long size);
}

//  @Query(value = """
//      SELECT p.*
//      FROM products p
//      JOIN product_category pc ON p.id = pc.product_id
//      JOIN categories c ON pc.category_id = c.id
//      WHERE c.category_name IN (:categories)
//      GROUP BY p.id
//      HAVING COUNT(DISTINCT c.id) = :size
//  """, nativeQuery = true)
