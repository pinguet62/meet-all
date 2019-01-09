package fr.pinguet62.meetall.photoproxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class PhotoProxyController {

    private final WebClient webClient = WebClient.builder().build();

    @GetMapping("/photo/{encodedUrl:.+}")
    public Mono<ResponseEntity<byte[]>> proxifyPhoto(@PathVariable String encodedUrl) {
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
