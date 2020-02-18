package fr.pinguet62.meetall.photoproxy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Tag(name = "Photo")
@RestController
public class PhotoProxyController {

    private final WebClient webClient;

    public PhotoProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Operation(
            summary = "Proxy for original image",
            responses = @ApiResponse(description = "The photo content", content = @Content(schema = @Schema(type = "string", format = "binary"))))
    @GetMapping("/photo/{encodedUrl:.+}")
    public Mono<ResponseEntity<byte[]>> proxifyPhoto(@PathVariable @Parameter(example = "https%3A%2F%2Fwww.google.com%2Fimages%2Fbranding%2Fgooglelogo%2F1x%2Fgooglelogo_color_150x54dp.png", description = "Encoded original URL") String encodedUrl) {
        String originalUrl = PhotoProxyEncoder.decode(encodedUrl);
        return webClient
                .get()
                .uri(originalUrl)
                .exchange()
                .flatMap(res -> res.bodyToMono(byte[].class)
                        .map(bytes -> ResponseEntity
                                .status(res.statusCode())
                                .headers(res.headers().asHttpHeaders())
                                .body(bytes)));
    }

}
