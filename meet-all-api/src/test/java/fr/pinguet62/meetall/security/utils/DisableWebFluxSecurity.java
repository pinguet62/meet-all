package fr.pinguet62.meetall.security.utils;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <ol>
 * <li>{@link WebFluxTest} is used by tests</li>
 * <li>This annotation declares {@link AutoConfigureWebTestClient}</li>
 * <li>This annotation <i>auto-import</i>s (based on {@code spring.factories}) theses <i>auto-configuration</i>s</li>
 * </ol>
 *
 * @example <pre>
 * @WebFluxTest(controllers = SampleController.class)
 * @DisableWebFluxSecurity
 * class SampleControllerTest {
 * </pre>
 */
@Target(TYPE)
@Retention(RUNTIME)
@ImportAutoConfiguration(exclude = {ReactiveSecurityAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class})
public @interface DisableWebFluxSecurity {
}
