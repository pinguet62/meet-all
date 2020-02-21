package fr.pinguet62.meetall.meet;

import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import lombok.Getter;

import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

class TransformedId {

    private static final String SEPARATOR = "#";

    /**
     * @see Credential#getId()
     */
    @Getter
    private final int credentialId;

    /**
     * @see ProfileDto#getId()
     * @see ConversationDto#getId()
     */
    @Getter
    private final String valueId;

    private TransformedId(int credentialId, String valueId) {
        this.credentialId = credentialId;
        this.valueId = requireNonNull(valueId);
    }

    public static String format(int credential, String valueId) {
        return String.format("%s%s%s", credential, SEPARATOR, valueId);
    }

    public static TransformedId parse(String transformed) {
        String[] values = transformed.split(SEPARATOR);
        return new TransformedId(parseInt(values[0]), values[1]);
    }

}
