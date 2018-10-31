package fr.pinguet62.meetall;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.function.Function;

import static org.hamcrest.Matchers.is;

public class MatcherUtils {

    /**
     * <pre>
     * URL url = new URL("http://api.com/user/42");
     * assertThat(url, with(URL::toString, containsString("user/42")));
     * </pre>
     */
    public static <MAIN, SUB> Matcher<MAIN> with(Function<MAIN, SUB> mapper, Matcher<? super SUB> matcher) {
        return new TypeSafeMatcher<MAIN>() {
            private SUB subValue;

            @Override
            public void describeTo(Description description) {
                description.appendValue(subValue).appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(MAIN mainValue) {
                subValue = mapper.apply(mainValue);
                return matcher.matches(subValue);
            }
        };
    }

    /**
     * Should be called only once by assertion,
     * because {@link MockWebServer#takeRequest()} pop 1 request by call.
     */
    public static Matcher<MockWebServer> takingRequest(Matcher<RecordedRequest> matcher) {
        return new TypeSafeMatcher<MockWebServer>() {
            @Override
            public void describeTo(Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(MockWebServer server) {
                try {
                    RecordedRequest request = server.takeRequest();
                    return matcher.matches(request);
                } catch (InterruptedException e) { // avoid "server" blocking when assertion fails.
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Matcher<RecordedRequest> header(String key, String value) {
        return header(key, is(value));
    }

    public static Matcher<RecordedRequest> header(String key, Matcher<? super String> matcher) {
        return new TypeSafeMatcher<RecordedRequest>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("headers with key " + key + " ").appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(RecordedRequest request) {
                String actual = request.getHeader(key);
                return matcher.matches(actual);
            }
        };
    }

    public static Matcher<RecordedRequest> url(Matcher<? super HttpUrl> matcher) {
        return new TypeSafeMatcher<RecordedRequest>() {
            private HttpUrl actual;

            @Override
            public void describeTo(Description description) {
                description.appendValue(actual).appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(RecordedRequest request) {
                actual = request.getRequestUrl();
                return matcher.matches(actual);
            }
        };
    }

}
