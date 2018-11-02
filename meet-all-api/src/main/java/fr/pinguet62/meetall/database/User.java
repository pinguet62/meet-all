package fr.pinguet62.meetall.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = true) // because of @Id
    private Integer id;

    @NotEmpty
    @Column(nullable = false)
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String password;

    private List<ProviderCredential> providerCredentials = new ArrayList<>();

    public User(String email, String password) {
        this(null, email, password);
    }

    public User(Integer id, String email, String password) {
        this.id = id;
        this.email = requireNonNull(email);
        this.password = requireNonNull(password);
    }

}
