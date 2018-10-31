package fr.pinguet62.meetall;

import fr.pinguet62.meetall.provider.Provider;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

public class ProviderIdValue {

    static final String SEPARATOR = ":";

    @Getter
    private final Provider provider;

    @Getter
    private final String valueId;

    private ProviderIdValue(Provider provider, String valueId) {
        this.provider = requireNonNull(provider);
        this.valueId = requireNonNull(valueId);
    }

    public static String format(ProviderIdValue providerIdValue) {
        return format(providerIdValue.getProvider(), providerIdValue.getValueId());
    }

    public static String format(Provider provider, String valueId) {
        return String.format("%s%s%s", provider.name(), SEPARATOR, valueId);
    }

    public static ProviderIdValue parse(String transformed) {
        String[] values = transformed.split(SEPARATOR);
        return new ProviderIdValue(Provider.valueOf(values[0]), values[1]);
    }

}
