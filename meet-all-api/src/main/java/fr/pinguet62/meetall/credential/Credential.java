package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

import static lombok.AccessLevel.PACKAGE;

@Document
@AllArgsConstructor
@Getter
public class Credential {

    @Id
    @Setter
    private String id;

    @NonNull
    private final String userId;

    /**
     * @see ProviderService#getId()
     */
    @NotEmpty
    @NonNull
    private final Provider provider;

    /**
     * The secret necessary to use target webservice.
     */
    @NotEmpty
    @NonNull
    @With(PACKAGE)
    private final String credential;

    @NotEmpty
    @NonNull
    @With(PACKAGE)
    private final String label;

}
