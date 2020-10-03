package fr.pinguet62.meetall.facebookcredential;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class RobotCredentialExtractorTest {

    RobotCredentialExtractor robotCredentialExtractor;

    WireMockServer facebookWireMockServer;

    @Before
    public void serverStartAndInit() {
        facebookWireMockServer = new WireMockRule(options().dynamicPort());
        facebookWireMockServer.start();

        robotCredentialExtractor = new RobotCredentialExtractor("http://localhost:" + facebookWireMockServer.port());
    }

    @After
    public void serverStop() {
        facebookWireMockServer.stop();
    }

    @Test
    public void test_returnsToken() {
        facebookWireMockServer
                .stubFor(get(new UrlPattern(new ContainsPattern("/dialog/oauth"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/ok/step0 - dialog_oauth.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/login/device-based/regular/login"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/ok/step1 - authorize.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/v3.0/dialog/oauth/confirm"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/ok/step2 - redirect.html"))));

        assertThat(robotCredentialExtractor.getTinderFacebookToken("test@pinguet62.fr", "AzErTy"), is("EAAGm0PX4ZCpsBAA3znvFZAkhp5ar1CMcdE1FfinDcrJZC6uflaHSqMwv8TCIoXis3ihh5sFjVda9HF9rGZAqtORf0X6p1TUhLfddmmyOjKSjKaqBQHmqyZAe6sgHdxhyTZBLPs2xZAjaYpgnwaSU0rR3XlOLRTn6YV9Nstnlhi7NaET3x8UXsz5E0dTYPZBhZBlwZD"));
    }

    @Test
    public void test_verifyCredentials_email() {
        facebookWireMockServer
                .stubFor(get(new UrlPattern(new ContainsPattern("/dialog/oauth"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyCredentials/step0 - dialog_oauth.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/login/device-based/regular/login"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyCredentials/step1.1 - unknown email.html"))));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> robotCredentialExtractor.getTinderFacebookToken("unknown@pinguet62.fr", "anything"));
        assertThat(exception.getMessage(), is("L’e-mail entré ne correspond à aucun compte. Veuillez créer un compte."));
    }

    @Test
    public void test_verifyCredentials_password() {
        facebookWireMockServer
                .stubFor(get(new UrlPattern(new ContainsPattern("/dialog/oauth"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyCredentials/step0 - dialog_oauth.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/login/device-based/regular/login"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyCredentials/step1.2 - bad password.html"))));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> robotCredentialExtractor.getTinderFacebookToken("test@pinguet62.fr", "bad"));
        assertThat(exception.getMessage(), is("Le mot de passe entré est incorrect. Vous l’avez oublié ?"));
    }

    /**
     * Conflict with {@code div[role='alert']}
     */
    @Test
    public void test_hasNotification() {
        facebookWireMockServer
                .stubFor(get(new UrlPattern(new ContainsPattern("/dialog/oauth"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/ok/step0 - dialog_oauth.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/login/device-based/regular/login"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyCredentials/step1.0 - has notifications.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/v3.0/dialog/oauth/confirm"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/ok/step2 - redirect.html"))));

        assertThat(robotCredentialExtractor.getTinderFacebookToken("test@pinguet62.fr", "AzErTy"), is("EAAGm0PX4ZCpsBAA3znvFZAkhp5ar1CMcdE1FfinDcrJZC6uflaHSqMwv8TCIoXis3ihh5sFjVda9HF9rGZAqtORf0X6p1TUhLfddmmyOjKSjKaqBQHmqyZAe6sgHdxhyTZBLPs2xZAjaYpgnwaSU0rR3XlOLRTn6YV9Nstnlhi7NaET3x8UXsz5E0dTYPZBhZBlwZD"));
    }

    @Test
    public void test_verifyAbsentOrProcessAndThrowError() {
        facebookWireMockServer
                .stubFor(get(new UrlPattern(new ContainsPattern("/dialog/oauth"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyAbsentOrProcessAndThrowError/step0 - dialog_oauth.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/login/device-based/regular/login"), false))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyAbsentOrProcessAndThrowError/step1 - login_device-based_regular_login.html"))));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/checkpoint"), false))
                        .inScenario("process")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyAbsentOrProcessAndThrowError/step2 - checkpoint.html")))
                        .willSetStateTo("step3"));
        facebookWireMockServer
                .stubFor(post(new UrlPattern(new ContainsPattern("/checkpoint"), false))
                        .inScenario("process")
                        .whenScenarioStateIs("step3")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(getHtml("/fr/pinguet62/meetall/facebookcredential/verifyAbsentOrProcessAndThrowError/step3 - checkpoint.html")))
                        .willSetStateTo("step4"));

        assertThrows(FacebookAccountLockedException.class, () -> robotCredentialExtractor.getTinderFacebookToken("test@pinguet62.fr", "AzErTy"));
    }

    private String getHtml(String filename) {
        try {
            return IOUtils.toString(getClass().getResourceAsStream(filename), UTF_8)
                    .replace(
                            "https://www.facebook.com",
                            "http://localhost:" + facebookWireMockServer.port());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
