package fr.pinguet62.meetall.config;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static fr.pinguet62.meetall.config.OpenApiConfig.BEARER;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;
import static org.springdoc.core.Constants.API_DOCS_URL;
import static org.springdoc.core.Constants.SWAGGER_CONFIG_URL;
import static org.springdoc.core.Constants.SWAGGER_UI_PATH;
import static org.springdoc.core.Constants.WEB_JARS_PREFIX_URL;

@Component
@SecurityScheme(name = BEARER, type = HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    public static final String BEARER = "bearer";

    @Value(API_DOCS_URL)
    private String apiDocsUrl;

    @Value(SWAGGER_UI_PATH)
    private String uiPath;

    @Value(WEB_JARS_PREFIX_URL)
    private String webJarsPrefixUrl;

    @Value(SWAGGER_CONFIG_URL)
    private String swaggerConfigUrl;

    public String[] getPublicRoutesPathMatchers() {
        return new String[]{
                apiDocsUrl,
                uiPath,
                webJarsPrefixUrl + "/**",
                swaggerConfigUrl};
    }

}
