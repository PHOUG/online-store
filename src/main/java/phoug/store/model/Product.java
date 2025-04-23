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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    // Связь с категориями
    @JsonIgnoreProperties({"products"})
    @ManyToMany(mappedBy = "products",
            fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Category> categories = new ArrayList<>();

    public Product() {
        // Конструктор по умолчанию
    }

    public long getId() {
        return id;
    }

    public String getArticle() {
        return article;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getBrand() {
        return brand;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
