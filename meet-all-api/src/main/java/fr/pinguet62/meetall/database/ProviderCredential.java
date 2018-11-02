package fr.pinguet62.meetall.database;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

import static java.util.Objects.requireNonNull;
import static javax.persistence.EnumType.STRING;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProviderCredential {

    @Id
    @Column(nullable = true) // because of @Id
    private Integer id;

    @ManyToOne(optional = false)
    @Column(nullable = false)
    private User user;

    /**
     * @see ProviderService#getId()
     */
    @NotEmpty
    @Column(nullable = false)
    @Enumerated(STRING)
    private Provider provider;

    /**
     * The secret necessary to use target webservice.
     */
    @NotEmpty
    @Column(nullable = false)
    private String credential;

    @NotEmpty
    @Column(nullable = false)
    private String label;

    public ProviderCredential(User user, Provider provider, String credential, String label) {
        this(null, user, provider, credential, label);
    }

    public ProviderCredential(Integer id, User user, Provider provider, String credential, String label) {
        this.id = id;
        this.user = requireNonNull(user);
        this.label = requireNonNull(label);
        this.provider = requireNonNull(provider);
        this.credential = requireNonNull(credential);
    }

}
