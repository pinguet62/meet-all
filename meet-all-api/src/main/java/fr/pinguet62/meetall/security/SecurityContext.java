package fr.pinguet62.meetall.security;

import fr.pinguet62.meetall.database.User;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

public class SecurityContext {

    /**
     * @see User#getId()
     */
    @Getter
    public final int userId;

    public SecurityContext(int userId) {
        this.userId = requireNonNull(userId);
    }

}
