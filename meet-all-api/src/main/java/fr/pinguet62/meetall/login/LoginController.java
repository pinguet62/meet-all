package fr.pinguet62.meetall.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "Login")
@RestController
@RequiredArgsConstructor
class LoginController {

    @NonNull
    private final LoginService loginService;

    /**
     * @param accessToken Facebook {@code access_token}.
     * @return The JWT token.
     */
    @PostMapping("/login")
    @Operation(summary = "Convert Facebook token to internal JWT token",
            responses = @ApiResponse(
                    description = "Application JWT for secured routes",
                    content = @Content(schema = @Schema(example = "xxx.yyyyy.zzzz"))))
    public Mono<String> login(@RequestParam("access_token") @Parameter(description = "Facebook OAuth token") String accessToken) {
        return loginService.login(accessToken);
    }

}
