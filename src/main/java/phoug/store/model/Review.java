package phoug.store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "Author name is required.")
    private String author;

    @Column(nullable = false)
    @NotEmpty(message = "Comment is required.")
    private String comment;

    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "Minimum rating is 0.01.")
    @DecimalMax(value = "5.00", message = "Maximum rating is 5.00.")
    private double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Review(String comment, String author, double rating, Product product, User user) {
        this.comment = comment;
        this.rating = rating;
        this.author = author;
        this.product = product;
        this.user = user;
    }
}
