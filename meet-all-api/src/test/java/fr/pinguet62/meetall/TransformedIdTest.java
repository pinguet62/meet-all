package fr.pinguet62.meetall;

import org.junit.Test;

import java.util.List;

import static fr.pinguet62.meetall.MatcherUtils.throwing;
import static fr.pinguet62.meetall.TransformedId.SEPARATOR;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TransformedIdTest {

    /**
     * @see TransformedId#format(int, String)
     * @see TransformedId#parse(String)
     */
    @Test
    public void shouldBeReversible() {
        for (int credentialId : List.of(0, 1, 42, 999_999)) {
            for (String valueId : List.of("1", "2", "qwerty")) {
                String transformed = TransformedId.format(credentialId, valueId);
                TransformedId transformedId = TransformedId.parse(transformed);

                assertThat(transformedId.getCredentialId(), is(credentialId));
                assertThat(transformedId.getValueId(), is(valueId));
            }
        }
    }

    /**
     * @see TransformedId#parse(String)
     */
    @Test
    public void parse_invalidInputShouldThrowException() {
        assertThat(() -> TransformedId.parse(null), is(throwing(RuntimeException.class)));
        for (String value : List.of(
                "", // empty
                42 + "id", // without separator
                42 + SEPARATOR, // missing credentialId
                SEPARATOR + "id", // missing ID
                "unknownProvider" + SEPARATOR + "id" // invalid credentialId
        )) {
            assertThat(() -> TransformedId.parse(value), is(throwing(RuntimeException.class)));
        }
    }

}
