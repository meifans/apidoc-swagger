package com.github.meifans.apidoc.maven.plugin.spring;

import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * @author pengfei.zhao
 */
public class SpringReader implements OpenApiReader {

    @Override
    public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {

    }

    @Override
    public OpenAPI read(Set<Class<?>> classes, Map<String, Object> resources) {
        return null;
    }
}
