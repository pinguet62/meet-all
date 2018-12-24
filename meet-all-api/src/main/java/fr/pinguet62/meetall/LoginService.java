package fr.pinguet62.meetall;

import fr.pinguet62.meetall.security.JwtTokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@Service
@Transactional
public class LoginService {

    private final FacebookApi facebookApi;
    private final JwtTokenGenerator jwtTokenGenerator;

    public LoginService(FacebookApi facebookApi, JwtTokenGenerator jwtTokenGenerator) {
        this.facebookApi = requireNonNull(facebookApi);
        this.jwtTokenGenerator = requireNonNull(jwtTokenGenerator);
    }

    /**
     * @param accessToken Facebook {@code access_token}
     * @return The JWT token.
     */
    public Mono<String> login(String accessToken) {
        return facebookApi.getMe(accessToken)
                .map(it -> jwtTokenGenerator.generateToken(it.getId()));
    }

}
