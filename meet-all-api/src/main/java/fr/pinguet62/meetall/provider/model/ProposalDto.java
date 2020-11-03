package fr.pinguet62.meetall.provider.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
public class ProposalDto {
    @Schema(example = "TINDER#9876543210",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NonNull
    @NotEmpty
    @With
    String id;

    @NonNull
    @NotNull
    @Valid
    @With
    ProfileDto profile;
}
