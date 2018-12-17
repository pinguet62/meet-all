package fr.pinguet62.meetall.provider.tinder;

public class TinderCredential {

    private final String oauthToken;

    private final String facebookToken;

    private final String facebookRefreshToken;

    public TinderCredential(String oauthToken, String facebookToken, String facebookRefreshToken) {
        this.oauthToken = oauthToken;
        this.facebookToken = facebookToken;
        this.facebookRefreshToken = facebookRefreshToken;
    }

}
