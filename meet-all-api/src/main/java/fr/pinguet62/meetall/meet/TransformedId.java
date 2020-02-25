package fr.pinguet62.meetall.meet;

import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

class TransformedId {

    static class InvalidTransformedId extends IllegalArgumentException {
        InvalidTransformedId(String message) {
            super(message);
        }
    }

    private static final String SEPARATOR = "#";

    /**
     * @see Credential#getId()
     */
    @Getter
    private final String credentialId;

    /**
     * @see ProfileDto#getId()
     * @see ConversationDto#getId()
     */
    @Getter
    private final String valueId;

    private TransformedId(String credentialId, String valueId) {
        this.credentialId = requireNonNull(credentialId);
        if (credentialId.isBlank()) throw new InvalidTransformedId("\"credential\" cannot be blank");
        this.valueId = requireNonNull(valueId);
        if (valueId.isBlank()) throw new InvalidTransformedId("\"credential\" cannot be blank");
    }

    public static String format(String credential, String valueId) throws InvalidTransformedId {
        if (credential == null) throw new InvalidTransformedId("\"credential\" cannot be null");
        if (valueId == null) throw new InvalidTransformedId("\"valueId\" cannot be null");
        if (credential.contains(SEPARATOR)) throw new InvalidTransformedId("\"valueId\" cannot contains internal separator");
        if (valueId.contains(SEPARATOR)) throw new InvalidTransformedId("\"valueId\" cannot contains separator");
        return String.format("%s%s%s", credential, SEPARATOR, valueId);
    }

    public static TransformedId parse(String transformed) throws InvalidTransformedId {
        if (transformed == null) throw new InvalidTransformedId("value cannot be null");
        String[] values = transformed.split(SEPARATOR);
        if (values.length == 1) throw new InvalidTransformedId("value doesn't contain separator");
        if (values.length > 2) throw new InvalidTransformedId("value contains more than 1 separator");
        return new TransformedId(values[0], values[1]);
    }

}
