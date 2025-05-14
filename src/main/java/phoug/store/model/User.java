package phoug.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Username is required.")
    private String username;

    @Column(nullable = false)
    @NotEmpty(message = "Password is required.")
    private String password;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "EMail is required.")
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "Phone number is required.")
    private String phone;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Basket basket;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}
