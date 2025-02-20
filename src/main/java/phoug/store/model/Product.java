package phoug.store.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private String name;        // Название товара
    private String article;     // Артикул для быстрого поиска
    private String description; // Описание
    private int quantity;       // Количество товаров
    private double price;       // Цена за штуку

    // Конструктор с параметрами
    public Product(String name, String article, String description,
                   int quantity, double price) {
        this.name = name;
        this.article = article;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }
}
