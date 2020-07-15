package fr.pinguet62.meetall.i18n;

import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;

import static fr.pinguet62.meetall.i18n.LocaleUtils.DEFAULT_LOCALE;

class ApplicationLocaleContextResolver extends AcceptHeaderLocaleContextResolver {

    public ApplicationLocaleContextResolver() {
        setDefaultLocale(DEFAULT_LOCALE);
        setSupportedLocales(LocaleUtils.getSupportedLocales());
    }

}
