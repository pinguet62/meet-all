package fr.pinguet62.meetall.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

class JwtToApplicationAuthenticationConverter {

    public static Mono<? extends AbstractAuthenticationToken> convert(Jwt jwt) throws AuthenticationException {
        String userId = jwt.getSubject();
        AbstractAuthenticationToken converted = new ApplicationAuthentication(userId);
        return Mono.just(converted);
    }

}
