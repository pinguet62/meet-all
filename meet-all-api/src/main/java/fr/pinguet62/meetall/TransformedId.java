package fr.pinguet62.meetall;

import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

public class TransformedId {

    static final String SEPARATOR = "#";

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
        this.credentialId = requireNonNull(credentialId);
        this.valueId = requireNonNull(valueId);
    }

    public static String format(TransformedId transformedId) {
        return format(transformedId.getCredentialId(), transformedId.getValueId());
    }

    public static String format(int credential, String valueId) {
        return String.format("%s%s%s", credential, SEPARATOR, valueId);
    }

    public static TransformedId parse(String transformed) {
        String[] values = transformed.split(SEPARATOR);
        return new TransformedId(Integer.parseInt(values[0]), values[1]);
    }

}
