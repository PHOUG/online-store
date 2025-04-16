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
@Table(name = "categories")
@ToString(exclude = "products")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotEmpty(message = "Category name is required.")
    @Size(min = 3, message = "Category name must be more than three (3) characters long.")
    private String categoryName;

    @JsonIgnoreProperties({"categories"})
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    //@JsonIgnore // Управляемая сторона, будет сериализована
    private List<Product> products = new ArrayList<>();

    // Конструктор по умолчанию
    public Category() {}

    // Конструктор с параметрами
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
