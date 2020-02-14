package fr.pinguet62.meetall.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import static java.util.Objects.requireNonNull;

/**
 * Data from current logged user, used by this application.
 * <p>
 * Example: current user's ID.
 */
public class ApplicationAuthentication extends AbstractAuthenticationToken {

    @Getter
    private final String userId;

    public ApplicationAuthentication(String userId) {
        super(null);
        this.userId = requireNonNull(userId);
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

}
