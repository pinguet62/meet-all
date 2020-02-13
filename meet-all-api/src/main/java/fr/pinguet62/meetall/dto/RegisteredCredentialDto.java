package fr.pinguet62.meetall.dto;

import fr.pinguet62.meetall.provider.Provider;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@ToString
@EqualsAndHashCode
@Getter
public class RegisteredCredentialDto {

    private final int id;

    @NotEmpty
    private final String label;

    @NotNull
    private final Provider provider;

    private final boolean ok;

    public RegisteredCredentialDto(int id, String label, Provider provider, boolean ok) {
        this.id = id;
        this.label = requireNonNull(label);
        this.provider = requireNonNull(provider);
        this.ok = ok;
    }

}
