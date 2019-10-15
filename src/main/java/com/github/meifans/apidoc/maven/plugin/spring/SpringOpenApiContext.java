package com.github.meifans.apidoc.maven.plugin.spring;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import io.swagger.v3.jaxrs2.integration.ServletConfigContextUtils;
import io.swagger.v3.jaxrs2.integration.ServletOpenApiConfigurationLoader;
import io.swagger.v3.jaxrs2.integration.ServletPathConfigurationLoader;
import io.swagger.v3.jaxrs2.integration.api.WebOpenApiContext;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiConfigurationLoader;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

public class SpringOpenApiContext<T extends SpringOpenApiContext> extends GenericOpenApiContext<SpringOpenApiContext> implements WebOpenApiContext {

    Logger LOGGER = LoggerFactory.getLogger(SpringOpenApiContext.class);
    private ServletContext servletContext;
    private ServletConfig servletConfig;

    @Override
    protected OpenApiReader buildReader(OpenAPIConfiguration openApiConfiguration) throws Exception {
        OpenApiReader reader = new SpringWebReader();
        reader.setConfiguration(openApiConfiguration);
        return reader;
    }

    @Override
    protected OpenApiScanner buildScanner(OpenAPIConfiguration openApiConfiguration) throws Exception {
        OpenApiScanner scanner = new SpringWebScanner();
        scanner.setConfiguration(openApiConfiguration);
        return scanner;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public T servletConfig(ServletConfig servletConfig) {

        if (!ServletConfigContextUtils.isServletConfigAvailable(servletConfig)) {
            return (T) this;
        }
        this.servletConfig = servletConfig;
        this.servletContext = servletConfig.getServletContext();
        return (T) this;
    }

    @Override
    protected List<ImmutablePair<String, String>> getKnownLocations() {

        List<ImmutablePair<String, String>> locations = new LinkedList<>(Arrays.asList(
                new ImmutablePair<>("servlet", ServletConfigContextUtils.OPENAPI_CONFIGURATION_LOCATION_KEY),
                new ImmutablePair<>("servletpath", "openapi-configuration.yaml"),
                new ImmutablePair<>("servletpath", "openapi-configuration.json"),
                new ImmutablePair<>("servletpath", "WEB-INF/openapi-configuration.yaml"),
                new ImmutablePair<>("servletpath", "WEB-INF/openapi-configuration.json"),
                new ImmutablePair<>("servletpath", "openapi.yaml"),
                new ImmutablePair<>("servletpath", "openapi.json"),
                new ImmutablePair<>("servletpath", "WEB-INF/openapi.yaml"),
                new ImmutablePair<>("servletpath", "WEB-INF/openapi.json")
        ));
        locations.addAll(super.getKnownLocations());
        locations.add(new ImmutablePair<>("servlet", ""));  // get config from init params
        return locations;
    }

    @Override
    protected Map<String, OpenApiConfigurationLoader> getLocationLoaders() {
        Map<String, OpenApiConfigurationLoader> map = super.getLocationLoaders();
        map.put("servlet", new ServletOpenApiConfigurationLoader(servletConfig));
        map.put("servletpath", new ServletPathConfigurationLoader(servletConfig));
        return map;
    }

}
