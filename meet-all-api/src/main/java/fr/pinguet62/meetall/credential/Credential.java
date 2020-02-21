package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import static java.util.Objects.requireNonNull;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Setter
public class Credential {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(nullable = true) // because of @Id
    private Integer id;

    @Column(nullable = false)
    private String userId;

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
    @Column(nullable = false, length = 4095)
    private String credential;

    @NotEmpty
    @Column(nullable = false)
    private String label;

    public Credential(String userId, Provider provider, String credential, String label) {
        this(null, userId, provider, credential, label);
    }

    public Credential(Integer id, String userId, Provider provider, String credential, String label) {
        this.id = id;
        this.userId = requireNonNull(userId);
        this.label = requireNonNull(label);
        this.provider = requireNonNull(provider);
        this.credential = requireNonNull(credential);
    }

}
