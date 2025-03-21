package phoug.store.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    // Связь с категориями
    @JsonIgnoreProperties({"products"})
    @ManyToMany(mappedBy = "products",
            fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Category> categories = new ArrayList<>();

    public Product() {
        // Конструктор по умолчанию
    }
}
