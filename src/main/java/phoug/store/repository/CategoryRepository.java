package phoug.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phoug.store.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
