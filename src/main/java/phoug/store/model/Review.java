package phoug.store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private double rating;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_article", nullable = false)
    private Product product;

    public Review() {}

    public Review(String comment, String author, float rating, Product product) {
        this.comment = comment;
        this.rating = rating;
        this.author = author;
        this.product = product;
    }
}

