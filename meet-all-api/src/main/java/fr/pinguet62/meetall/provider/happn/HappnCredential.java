package fr.pinguet62.meetall.provider.happn;

/**
 * Given by {@code POST /connect/oauth/token}.
 */
public class HappnCredential {

    private final String oauthToken;

    private final String refreshToken;

    public HappnCredential(String oauthToken, String refreshToken) {
        this.oauthToken = oauthToken;
        this.refreshToken = refreshToken;
    }

}
