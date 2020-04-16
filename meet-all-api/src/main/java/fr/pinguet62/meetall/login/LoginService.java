package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.security.JwtTokenGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class LoginService {

    @NonNull
    private final FacebookApi facebookApi;
    @NonNull
    private final JwtTokenGenerator jwtTokenGenerator;

    /**
     * @param facebookToken Facebook {@code access_token}.
     * @return The JWT token.
     */
    public Mono<String> login(String facebookToken) {
        return facebookApi.getMe(facebookToken)
                .map(FacebookApi.MeResponseDto::getId)
                .map(jwtTokenGenerator::generateToken);
    }

}
