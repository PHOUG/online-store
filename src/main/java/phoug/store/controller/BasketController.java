package phoug.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phoug.store.model.Basket;
import phoug.store.service.BasketService;

@RestController
@RequestMapping("/baskets")
public class BasketController {

    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @Operation(summary = "Создать новую корзинку")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Корзина успешно создана"),
        @ApiResponse(responseCode = "400", description = "Неверный ввода")
    })
    @PostMapping
    public ResponseEntity<Basket> createBasket(@RequestBody Basket basket) {
        Basket created = basketService.createBasket(basket);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Получить все корзины")
    @ApiResponse(responseCode = "200", description = "Лист всех корзин отправлен")
    @GetMapping
    public ResponseEntity<List<Basket>> getAllBaskets() {
        List<Basket> list = basketService.getAllBaskets();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Получить корзину по её ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Корзина найдена"),
        @ApiResponse(responseCode = "404", description = "Корзина не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Basket> getBasketById(@PathVariable Long id) {
        Basket basket = basketService.getBasketById(id);
        return ResponseEntity.ok(basket);
    }

    @Operation(summary = "Обновить существующую корзину")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Корзина обновлена"),
        @ApiResponse(responseCode = "404", description = "Корзина не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Basket> updateBasket(
            @PathVariable Long id,
            @RequestBody Basket basketDetails) {
        Basket updated = basketService.updateBasket(id, basketDetails);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить корзину")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Корзина удалена"),
        @ApiResponse(responseCode = "404", description = "Корзина не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasket(@PathVariable Long id) {
        basketService.deleteBasket(id);
        return ResponseEntity.noContent().build();
    }
}