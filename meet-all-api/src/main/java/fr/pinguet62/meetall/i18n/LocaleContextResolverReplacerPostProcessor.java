package fr.pinguet62.meetall.i18n;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.EnableWebFluxConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;

/**
 * By default, both define default {@link LocaleContextResolver} in {@link WebFluxConfigurationSupport}:
 * <ul>
 * <li>{@link WebFluxAutoConfiguration} importing {@link EnableWebFluxConfiguration}</li>
 * <li>{@link EnableWebFlux} importing {@link DelegatingWebFluxConfiguration}</li>
 * </ul>
 * <p>
 * To customize {@link LocaleContextResolver} it's necessary to create custom {@link DelegatingWebFluxConfiguration},
 * but this disable {@link WebFluxAutoConfiguration}.
 * <p>
 * This solution keep default auto-configuration, by <b>replacing dynamically</b> the default
 * {@link LocaleContextResolver} by custom {@link ApplicationLocaleContextResolver}.
 */
@Configuration
class LocaleContextResolverReplacerPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof AcceptHeaderLocaleContextResolver)) {
            return bean;
        }

        return new ApplicationLocaleContextResolver();
    }
}
