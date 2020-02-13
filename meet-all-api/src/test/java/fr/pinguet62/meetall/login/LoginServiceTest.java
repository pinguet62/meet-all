package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.login.FacebookApi.MeResponseDto;
import fr.pinguet62.meetall.security.JwtTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    FacebookApi facebookApi;
    JwtTokenGenerator jwtTokenGenerator;
    LoginService loginService;

    @BeforeEach
    void initMock() {
        facebookApi = mock(FacebookApi.class);
        jwtTokenGenerator = mock(JwtTokenGenerator.class);
        loginService = new LoginService(facebookApi, jwtTokenGenerator);
    }

    @Test
    void login() {
        final String accessToken = "accessToken";
        final String jwtToken = "jwtToken";
        final String userId = "userId";

        when(facebookApi.getMe(accessToken)).thenReturn(Mono.just(new MeResponseDto(userId)));
        when(jwtTokenGenerator.generateToken(userId)).thenReturn(jwtToken);

        Mono<String> result = loginService.login(accessToken);

        assertThat(result.block(), is(jwtToken));
    }
}
