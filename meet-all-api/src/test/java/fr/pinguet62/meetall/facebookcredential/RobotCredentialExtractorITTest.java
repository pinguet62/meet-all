package fr.pinguet62.meetall.facebookcredential;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("TODO IT-test")
public class RobotCredentialExtractorITTest {

    private static final String EMAIL = ""; // TODO IT-test
    private static final String PASSWORD = ""; // TODO IT-test

    @Test
    public void test_getHappnFacebookToken() {
        System.out.println(new RobotCredentialExtractor()
                .getHappnFacebookToken(EMAIL, PASSWORD)
                .block());
    }

    @Test
    public void test_getOnceFacebookToken() {
        System.out.println(new RobotCredentialExtractor()
                .getOnceFacebookToken(EMAIL, PASSWORD)
                .block());
    }

    @Test
    public void test_getTinderFacebookToken() {
        System.out.println(new RobotCredentialExtractor()
                .getTinderFacebookToken(EMAIL, PASSWORD)
                .block());
    }

}
