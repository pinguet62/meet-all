package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.login.FacebookApi.MeResponseDto;
import fr.pinguet62.meetall.security.JwtTokenGenerator;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginServiceTest {

    private FacebookApi facebookApi;
    private JwtTokenGenerator jwtTokenGenerator;
    private LoginService loginService;

    @Before
    public void initMock() {
        facebookApi = mock(FacebookApi.class);
        jwtTokenGenerator = mock(JwtTokenGenerator.class);
        loginService = new LoginService(facebookApi, jwtTokenGenerator);
    }

    @Test
    public void login() {
        final String accessToken = "accessToken";
        final String jwtToken = "jwtToken";
        final String userId = "userId";

        when(facebookApi.getMe(accessToken)).thenReturn(Mono.just(new MeResponseDto(userId)));
        when(jwtTokenGenerator.generateToken(userId)).thenReturn(jwtToken);

        Mono<String> result = loginService.login(accessToken);

        assertThat(result.block(), is(jwtToken));
    }

}
