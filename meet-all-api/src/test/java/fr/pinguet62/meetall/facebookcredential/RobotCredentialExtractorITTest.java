package fr.pinguet62.meetall.facebookcredential;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static fr.pinguet62.meetall.facebookcredential.RobotCredentialExtractor.parseHtml;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class RobotCredentialExtractorITTest {

    final String EMAIL = ""; // TODO IT-test
    final String PASSWORD = ""; // TODO IT-test

    RobotCredentialExtractor robotCredentialExtractor = new RobotCredentialExtractor();

    @Disabled("TODO IT-test")
    @Test
    void getHappnFacebookToken() {
        assertThat(robotCredentialExtractor.getHappnFacebookToken(EMAIL, PASSWORD), is(notNullValue()));
    }

    @Disabled("TODO IT-test")
    @Test
    void getOnceFacebookToken() {
        assertThat(robotCredentialExtractor.getOnceFacebookToken(EMAIL, PASSWORD), is(notNullValue()));
    }

    @Disabled("TODO IT-test")
    @Test
    void getTinderFacebookToken() {
        assertThat(robotCredentialExtractor.getTinderFacebookToken(EMAIL, PASSWORD), is(notNullValue()));
    }

    @Test
    void parseHtml_shouldExtractJWTTokenFromRedirectScriptInBody() {
        String html = "<html><head><script type=\"text/javascript\">window.location.href=\"fb464891386855067:\\/\\/authorize\\/#access_token=EAAGm0PX4ZCpsBAO2UHRDH3XdzXocd7ldVTIGeB7tmlx4qh8T1xzD3puQyk8EgjvZC6nOjL7i07x6qFJud31GNvnGFbKKsaAr6Ml7pJkzuc1DPQX3dimt39cCr5fDOGX63UARQEqatv63gHBAaB6bLizNCe6cTEFSC3IyZAyyQn7qva5UDYzjudyKAQQXuUZD&data_access_expiration_time=1595148525&expires_in=4274\";</script></head></html>";
        assertThat(parseHtml(html), is("EAAGm0PX4ZCpsBAO2UHRDH3XdzXocd7ldVTIGeB7tmlx4qh8T1xzD3puQyk8EgjvZC6nOjL7i07x6qFJud31GNvnGFbKKsaAr6Ml7pJkzuc1DPQX3dimt39cCr5fDOGX63UARQEqatv63gHBAaB6bLizNCe6cTEFSC3IyZAyyQn7qva5UDYzjudyKAQQXuUZD"));
    }
}
