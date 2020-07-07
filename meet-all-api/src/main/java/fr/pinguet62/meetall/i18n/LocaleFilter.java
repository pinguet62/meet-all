package fr.pinguet62.meetall.i18n;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.i18n.LocaleContextResolver;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.i18n.ReactiveLocaleContextHolder.withLocaleContext;

@RequiredArgsConstructor
@Component
@Import({
        ApplicationLocaleContextResolver.class,
        LocaleContextResolverReplacerPostProcessor.class, // not auto-imported by @WebFluxTest
})
class LocaleFilter implements WebFilter {

    @NonNull
    private final LocaleContextResolver localeContextResolver;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .subscriberContext(withLocaleContext(localeContextResolver.resolveLocaleContext(exchange).getLocale()));
    }
}
