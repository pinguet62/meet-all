package fr.pinguet62.meetall.i18n;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.in;

public class LocaleUtilsTest {

    @Test
    public void DEFAULT_LOCALE() {
        assertThat(LocaleUtils.DEFAULT_LOCALE, in(LocaleUtils.getSupportedLocales()));
    }

    @Test
    public void getSupportedLocales() {
        assertThat(LocaleUtils.getSupportedLocales(), containsInAnyOrder(new Locale("en"), new Locale("fr")));
    }

}
