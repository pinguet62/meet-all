package fr.pinguet62.meetall.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static javax.persistence.FetchType.EAGER;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    @Column(nullable = true) // because of @Id
    private Integer id;

    @NotEmpty
    @Column(nullable = false)
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", fetch = EAGER)
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
