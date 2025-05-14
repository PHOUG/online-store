package phoug.store.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phoug.store.model.Basket;
import phoug.store.repository.BasketRepository;
import phoug.store.service.BasketService;

@Service
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;

    @Autowired
    public BasketServiceImpl(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Override
    public Basket createBasket(Basket basket) {
        return basketRepository.save(basket);
    }

    @Override
    public Basket getBasketById(Long id) {
        return basketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Basket not found with id: " + id));
    }

    @Override
    public List<Basket> getAllBaskets() {
        return basketRepository.findAll();
    }

    @Override
    public Basket updateBasket(Long id, Basket basketDetails) {
        Basket basket = getBasketById(id);

        basket.setUser(basketDetails.getUser());
        basket.setProducts(basketDetails.getProducts());
        return basketRepository.save(basket);
    }

    @Override
    public void deleteBasket(Long id) {
        Basket basket = getBasketById(id);
        basketRepository.delete(basket);
    }
}