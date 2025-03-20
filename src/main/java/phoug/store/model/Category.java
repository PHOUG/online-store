package phoug.store.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();

    // Конструктор по умолчанию
    public Category() {}

    // Конструктор с параметрами
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
