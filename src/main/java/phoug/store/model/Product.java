package phoug.store.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "products")
@ToString(exclude = {"reviews", "categories"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    @NotEmpty(message = "Article is required.")
    @Size(min = 2, message = "Category name must be more than two (2) characters long.")
    private String article;     // Артикул для быстрого поиска

    @NotEmpty(message = "Product name is required.")
    private String name;        // Название товара

    @NotNull(message = "Product price is required.")
    @DecimalMin(value = "0.01", message = "The minimum price of the product is 0.01.")
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
