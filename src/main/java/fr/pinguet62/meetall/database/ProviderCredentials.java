package fr.pinguet62.meetall.database;

import fr.pinguet62.meetall.provider.Provider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import static java.util.Objects.requireNonNull;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProviderCredentials {

    @Id
    @Column(nullable = true) // because of @Id
    private Integer id;

    /**
     * @see Provider#getId()
     */
    @NotEmpty
    @Column(nullable = false)
    private String providerId;

    /**
     * The secret necessary to use target webservice.
     */
    @NotEmpty
    private String credentials;

    public ProviderCredentials(String providerId, String credentials) {
        this(null, providerId, credentials);
    }

    public ProviderCredentials(Integer id, String providerId, String credentials) {
        this.id = id;
        this.providerId = requireNonNull(providerId);
        this.credentials = requireNonNull(credentials);
    }

}
