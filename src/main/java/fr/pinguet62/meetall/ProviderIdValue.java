package fr.pinguet62.meetall;

import lombok.Getter;

import static java.util.Objects.requireNonNull;

public class ProviderIdValue {

    @Getter
    private final String providerId;

    @Getter
    private final String valueId;

    private ProviderIdValue(String providerId, String valueId) {
        this.providerId = requireNonNull(providerId);
        this.valueId = requireNonNull(valueId);
    }

    public static String format(ProviderIdValue providerIdValue) {
        return format(providerIdValue.getProviderId(), providerIdValue.getValueId());
    }

    public static String format(String providerId, String valueId) {
        return String.format("%s:%s", providerId, valueId);
    }

    public static ProviderIdValue parse(String encrypted) {
        String[] values = encrypted.split(":");
        return new ProviderIdValue(values[0], values[1]);
    }

}
