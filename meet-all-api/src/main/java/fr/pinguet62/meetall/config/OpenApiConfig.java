package fr.pinguet62.meetall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.springdoc.core.Constants.API_DOCS_URL;
import static org.springdoc.core.Constants.SWAGGER_CONFIG_URL;
import static org.springdoc.core.Constants.SWAGGER_UI_PATH;
import static org.springdoc.core.Constants.WEB_JARS_PREFIX_URL;

@Component
public class OpenApiConfig {

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
