package com.github.meifans.apidoc.maven.plugin.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

public class SpringOpenApiContext<T extends SpringOpenApiContext> extends GenericOpenApiContext<SpringOpenApiContext> implements OpenApiContext {
    Logger LOGGER = LoggerFactory.getLogger(SpringOpenApiContext.class);

    @Override
    protected OpenApiReader buildReader(OpenAPIConfiguration openApiConfiguration) throws Exception {
        OpenApiReader reader = new SpringReader();
        return reader;
    }

    @Override
    protected OpenApiScanner buildScanner(OpenAPIConfiguration openApiConfiguration) throws Exception {
        OpenApiScanner scanner = new SpringOpenApiScanner();
        return scanner;
    }
}
