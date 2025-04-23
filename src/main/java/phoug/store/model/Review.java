package phoug.store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "reviews")
@ToString(exclude = "product")
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
    @NotNull(message = "Product rating is required.")
    @DecimalMin(value = "0.01", message = "The minimum rating of the product is 0.01.")
    @DecimalMax(value = "5.00", message = "The maximum rating of the product is 5.00.")
    private double rating;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_article", nullable = false)
    private Product product;

    public Review() {}

    public Review(String comment, String author, float rating, Product product) {
        this.comment = comment;
        this.rating = rating;
        this.author = author;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public double getRating() {
        return rating;
    }

    public Product getProduct() {
        return product;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

