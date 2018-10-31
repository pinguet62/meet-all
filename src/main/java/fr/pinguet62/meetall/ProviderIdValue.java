package fr.pinguet62.meetall;

import fr.pinguet62.meetall.provider.Provider;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

public class ProviderIdValue {

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
        return String.format("%s:%s", provider.name(), valueId);
    }

    public static ProviderIdValue parse(String encrypted) {
        String[] values = encrypted.split(":");
        return new ProviderIdValue(Provider.valueOf(values[0]), values[1]);
    }

}
