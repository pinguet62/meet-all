package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    /**
     * @param email    {@link User#getEmail()}
     * @param password {@link User#getPassword()}
     * @return The token.
     */
    @PostMapping("/user")
    @ResponseStatus(CREATED)
    public Mono<String> createAccount(@RequestPart String email, @RequestPart String password) {
        return loginService.createAccount(email, password);
    }

    /**
     * @param email    {@link User#getEmail()}
     * @param password {@link User#getPassword()}
     * @return The token.
     */
    @PostMapping("/login")
    public Mono<String> login(@RequestPart String email, @RequestPart String password) {
        return loginService.login(email, password);
    }

}
