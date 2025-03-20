package phoug.store.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String article;     // Артикул для быстрого поиска

    private String name;        // Название товара
    private double price;       // Цена за штуку
    private String description;
    private String image;
    private String brand;
    private String size;
    private String color;

    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // Конструктор по умолчанию
    public Product() {}

    // Конструктор с параметрами
    public Product(String name, String article, double price,
                   String description, String image, String brand,
                   String size, String color) {
        this.name = name;
        this.article = article;
        this.price = price;
        this.description = description;
        this.image = image;
        this.brand = brand;
        this.size = size;
        this.color = color;
    }
}
