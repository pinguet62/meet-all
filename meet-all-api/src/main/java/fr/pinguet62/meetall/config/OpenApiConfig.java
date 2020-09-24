package fr.pinguet62.meetall.config;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.ui.AbstractSwaggerWelcome;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static fr.pinguet62.meetall.config.OpenApiConfig.BEARER;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;
import static org.springdoc.core.Constants.SWAGGGER_CONFIG_FILE;
import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

@RequiredArgsConstructor
@Component
@SecurityScheme(name = BEARER, type = HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    public static final String BEARER = "bearer";

    private final SpringDocConfigProperties springDocConfigProperties;
    private final SwaggerUiConfigProperties swaggerUiConfigProperties;
    private final SwaggerUiConfigParameters swaggerUiConfigParameters;

    public String[] getPublicRoutesPathMatchers() {
        return new String[]{
                springDocConfigProperties.getApiDocs().getPath(), // default: "/v3/api-docs"
                springDocConfigProperties.getWebjars().getPrefix() + "/**", // default: "/webjars"
                swaggerUiConfigParameters.getPath(), // default: "/swagger-ui.html"
                swaggerUiConfigPropertiesConfigUrl()}; // default: "/v3/api-docs/swagger-config"
    }

    /**
     * @see AbstractSwaggerWelcome#buildConfigUrl(String, UriComponentsBuilder)
     */
    private String swaggerUiConfigPropertiesConfigUrl() {
        if (StringUtils.isEmpty(swaggerUiConfigProperties.getConfigUrl()))
            return springDocConfigProperties.getApiDocs().getPath() + DEFAULT_PATH_SEPARATOR + SWAGGGER_CONFIG_FILE;
        else
            return swaggerUiConfigProperties.getConfigUrl();
    }

}
