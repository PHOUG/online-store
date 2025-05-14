package phoug.store.service;

import java.util.List;
import phoug.store.model.Basket;

public interface BasketService {
    Basket createBasket(Basket basket);

    Basket getBasketById(Long id);

    List<Basket> getAllBaskets();

    Basket updateBasket(Long id, Basket basket);

    void deleteBasket(Long id);
}