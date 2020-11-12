package fr.pinguet62.meetall.i18n;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Locale;

public class ReactiveLocaleContextHolder {

    static final Object KEY = Locale.class;

    public static Mono<Locale> getLocale() {
        return Mono.deferContextual(context -> Mono.just(context.get(KEY)));
    }

    public static Context withLocaleContext(Locale locale) {
        return Context.of(KEY, locale);
    }
}
