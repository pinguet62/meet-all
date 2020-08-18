package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.With;
import lombok.experimental.NonFinal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

import static lombok.AccessLevel.PACKAGE;

@Value
@Document
public class Credential {
    @NonFinal
    @Setter(PACKAGE)
    @Id
    String id;

    @NonNull
    String userId;

    /**
     * @see ProviderService#getId()
     */
    @NonNull
    @NotEmpty
    Provider provider;

    /**
     * The secret necessary to use target webservice.
     */
    @NonNull
    @With(PACKAGE)
    @NotEmpty
    String credential;

    @NotEmpty
    @NonNull
    @With(PACKAGE)
    String label;
}
