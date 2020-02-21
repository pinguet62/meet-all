package fr.pinguet62.meetall.provider.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@ToString
@EqualsAndHashCode
@Getter
public class ProposalDto {

    @Schema(example = "TINDER#9876543210",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NotNull
    private final String id;

    @NotNull
    @Valid
    private final ProfileDto profile; // TODO LazyProfileDto

    public ProposalDto(String id, ProfileDto profile) {
        this.id = requireNonNull(id);
        this.profile = requireNonNull(profile);
    }

    public ProposalDto withId(String value) {
        if (value.equals(this.id)) return this;
        return new ProposalDto(value, this.profile);
    }

    public ProposalDto withProfile(ProfileDto value) {
        if (value.equals(this.profile)) return this;
        return new ProposalDto(this.id, value);
    }

}
