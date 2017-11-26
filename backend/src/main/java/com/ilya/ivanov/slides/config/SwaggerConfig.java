package com.ilya.ivanov.slides.config;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

/**
 * Created by i.ivanov on 11/24/17.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String APPLICATION_ROUTES = "(?:/resources/.*|/api/.*|/admin/.*)";

    @Value("${security.jwt.client-id}")
    private String clientId;

    @Value("${security.jwt.client-secret}")
    private String clientSecret;

    @Value("${security.basic.realm}")
    private String realm;

    @Bean
    public Docket api() {
        return new Docket(SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex(APPLICATION_ROUTES))
                .build();
    }

    @Bean
    public SecurityConfiguration security() {
        return new springfox.documentation.swagger.web.SecurityConfiguration(
                clientId, clientSecret, realm,
                "Slides",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidGVzdGp3dHJlc291cmNlaWQiXSwidXNlcl9uYW1lIjoiYWRtaW4uYWRtaW4iLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiaWQiOjIsImV4cCI6MTUxMTczNzc3OCwiYXV0aG9yaXRpZXMiOlsiU1RBTkRBUkRfVVNFUiIsIkFETUlOX1VTRVIiXSwianRpIjoiZGYxNjY1ODEtNGJhOS00ODgxLWI3OWEtOGQyNjk1YzViZWI4IiwiY2xpZW50X2lkIjoidGVzdGp3dGNsaWVudGlkIn0.qGPJgsu5DivEYBKq7X3BW7P81sJ4wudEmTX8TEvtGN4",
                ApiKeyVehicle.HEADER,
                "Authorization",
                ",");
    }
}