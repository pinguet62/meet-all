package fr.pinguet62.meetall.dto;

import fr.pinguet62.meetall.provider.Provider;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@Getter
public class RegisteredCredentialDto {

    @NotNull
    private final int id;

    @NotEmpty
    private final String label;

    @NotNull
    private final Provider provider;

    public RegisteredCredentialDto(int id, String label, Provider provider) {
        this.id = id;
        this.label = requireNonNull(label);
        this.provider = requireNonNull(provider);
    }

}
