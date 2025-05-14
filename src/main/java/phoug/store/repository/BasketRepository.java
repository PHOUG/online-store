package phoug.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phoug.store.model.Basket;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
}
