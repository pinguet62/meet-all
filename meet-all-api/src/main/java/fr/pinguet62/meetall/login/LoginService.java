package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.security.JwtTokenGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Transactional
@Service
@RequiredArgsConstructor
class LoginService {

    @NonNull
    private final FacebookApi facebookApi;
    @NonNull
    private final JwtTokenGenerator jwtTokenGenerator;

    /**
     * @param accessToken Facebook {@code access_token}
     * @return The JWT token.
     */
    public Mono<String> login(String accessToken) {
        return facebookApi.getMe(accessToken)
                .map(FacebookApi.MeResponseDto::getId)
                .map(jwtTokenGenerator::generateToken);
    }

}
