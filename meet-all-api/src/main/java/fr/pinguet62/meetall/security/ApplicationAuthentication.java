package fr.pinguet62.meetall.security;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * Data from current logged user, used by this application.
 * <p>
 * Example: current user's ID.
 */
public class ApplicationAuthentication implements Authentication {

    @Getter
    private final String userId;

    public ApplicationAuthentication(String userId) {
        this.userId = requireNonNull(userId);
    }

    @Override
    public String getName() {
        return userId;
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
    public Object getDetails() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

}
