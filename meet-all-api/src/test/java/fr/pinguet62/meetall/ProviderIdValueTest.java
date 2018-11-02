package fr.pinguet62.meetall;

import fr.pinguet62.meetall.provider.Provider;
import org.junit.Test;

import static fr.pinguet62.meetall.MatcherUtils.throwing;
import static fr.pinguet62.meetall.ProviderIdValue.SEPARATOR;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProviderIdValueTest {

    /**
     * @see ProviderIdValue#format(Provider, String)
     * @see ProviderIdValue#parse(String)
     */
    @Test
    public void shouldBeReversible() {
        for (Provider provider : Provider.values()) {
            for (String valueId : asList("1", "2", "qwerty")) {
                String transformed = ProviderIdValue.format(provider, valueId);
                ProviderIdValue providerIdValue = ProviderIdValue.parse(transformed);

                assertThat(providerIdValue.getProvider(), is(provider));
                assertThat(providerIdValue.getValueId(), is(valueId));
            }
        }
    }

    /**
     * @see ProviderIdValue#parse(String)
     */
    @Test
    public void parse_invalidInputShouldThrowException() {
        for (String value : asList(
                null,
                "", // empty
                Provider.values()[0].name() + "id", // without separator
                Provider.values()[0].name() + SEPARATOR, // missing Provider
                SEPARATOR + "id", // missing ID
                "unknownProvider" + SEPARATOR + "id" // invalid Provider
        )) {
            assertThat(() -> ProviderIdValue.parse(value), is(throwing(RuntimeException.class)));
        }
    }

}
