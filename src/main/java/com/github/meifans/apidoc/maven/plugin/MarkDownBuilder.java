package com.github.meifans.apidoc.maven.plugin;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;

/**
 * @author pengfei.zhao
 */
public class MarkDownBuilder {

    private OpenAPI openAPI;

    public MarkDownBuilder() {
    }

    public MarkDownBuilder openAPI(OpenAPI openAPI) {
        this.openAPI = openAPI;
        return this;
    }

    public void toFoler() {
        openAPI.getPaths().forEach((method, api) -> {
            generate(method, api);
        });
    }

    private void generate(String method, PathItem api) {
    }

}
