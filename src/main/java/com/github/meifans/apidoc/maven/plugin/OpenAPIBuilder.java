package com.github.meifans.apidoc.maven.plugin;

import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;

/**
 * @author pengfei.zhao
 */
public final class OpenAPIBuilder {

    private OpenAPI openAPI;

    public OpenAPIBuilder() {
        this.openAPI = new OpenAPI();
        this.openAPI.setPaths(new Paths());
        openAPI.setComponents(new Components());
    }

    public void addPathItem(String name, PathItem item) {

    }

    public void addOperation(String path, Operation operation, RequestMethod method) {

    }

    public OpenAPI build() {
        return this.openAPI;
    }
}
