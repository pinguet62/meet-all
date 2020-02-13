package fr.pinguet62.meetall.meet;

import fr.pinguet62.meetall.meet.TransformedId.InvalidTransformedId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.pinguet62.meetall.MatcherUtils.throwing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TransformedIdTest {

    /**
     * @see TransformedId#format(String, String)
     * @see TransformedId#parse(String)
     */
    @Test
    void shouldBeReversible() {
        for (String credentialId : List.of("0", "1", "42", "999_999")) {
            for (String valueId : List.of("1", "2", "qwerty")) {
                String transformed = TransformedId.format(credentialId, valueId);
                TransformedId transformedId = TransformedId.parse(transformed);

                assertThat(transformedId.getCredentialId(), is(credentialId));
                assertThat(transformedId.getValueId(), is(valueId));
            }
        }
    }

    @Nested
    class parse {
        /**
         * @see TransformedId#parse(String)
         */
        @Test
        void invalidInputShouldThrowException() {
            assertThat(() -> TransformedId.parse(null), is(throwing(InvalidTransformedId.class)));
            for (String value : List.of(
                    "", // empty
                    "42id", // without separator
                    "42#", // missing credentialId
                    "#id" // missing ID
            )) {
                assertThat(() -> TransformedId.parse(value), is(throwing(InvalidTransformedId.class)));
            }
        }
    }
}
