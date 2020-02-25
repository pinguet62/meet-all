package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@ToString
@EqualsAndHashCode
@Getter
class RegisteredCredentialDto {

    @Schema(required = true, description = "Internal ID")
    private final String id;

    @NotEmpty
    @Schema(description = "UI information")
    private final String label;

    @NotNull
    private final Provider provider;

    @Schema(required = true, description = "If the credential is actually valid")
    private final boolean ok;

    public RegisteredCredentialDto(String id, String label, Provider provider, boolean ok) {
        this.id = id;
        this.label = requireNonNull(label);
        this.provider = requireNonNull(provider);
        this.ok = ok;
    }

}
