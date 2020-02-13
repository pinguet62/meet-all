package fr.pinguet62.meetall.login;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LoginController {

    static final String TOKEN_PARAM = "access_token";

    @NonNull
    private final LoginService loginService;

    /**
     * @param accessToken Facebook {@code access_token}.
     * @return The JWT token.
     */
    @PostMapping("/login")
    public Mono<String> login(@RequestParam(TOKEN_PARAM) String accessToken) {
        return loginService.login(accessToken);
    }

}
