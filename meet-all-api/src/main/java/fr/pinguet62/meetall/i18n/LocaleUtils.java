package fr.pinguet62.meetall.i18n;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toList;

public class LocaleUtils {

    public static Locale DEFAULT_LOCALE = ENGLISH;

    static {
        Locale.setDefault(DEFAULT_LOCALE);
    }

    public static List<Locale> getSupportedLocales() {
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(LocaleUtils.class.getClassLoader());
        Resource[] resources;
        try {
            resources = resourceLoader.getResources("classpath*:/messages_*.properties");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Stream.concat(
                Stream.of(DEFAULT_LOCALE),
                Arrays.stream(resources)
                        .map(Resource::getFilename)
                        .map(it -> it.replaceFirst("^messages_", ""))
                        .map(it -> it.replaceFirst("\\.properties$", ""))
                        .map(Locale::forLanguageTag))
                .collect(toList());
    }

}
