package com.github.meifans.apidoc.maven.plugin.spring;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;

import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.api.OpenApiContext;

/**
 * @author meifans
 */
public class SpringOpenApiContextBuilder<T extends SpringOpenApiContextBuilder> extends GenericOpenApiContextBuilder<SpringOpenApiContextBuilder> {

    protected ServletConfig servletConfig;

    @Override
    public OpenApiContext buildContext(boolean init) throws OpenApiConfigurationException {
        if (StringUtils.isBlank(ctxId)) {
            ctxId = OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT;
        }

        OpenApiContext ctx = OpenApiContextLocator.getInstance().getOpenApiContext(ctxId);

        if (ctx == null) {
            OpenApiContext rootCtx = OpenApiContextLocator.getInstance().getOpenApiContext(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT);
            ctx = new SpringOpenApiContext()
                    .servletConfig(servletConfig)
                    .openApiConfiguration(openApiConfiguration)
                    .id(ctxId)
                    .parent(rootCtx);

            if (ctx.getConfigLocation() == null && configLocation != null) {
                ((GenericOpenApiContext) ctx).configLocation(configLocation);
            }
            if (((GenericOpenApiContext) ctx).getResourcePackages() == null && resourcePackages != null) {
                ((GenericOpenApiContext) ctx).resourcePackages(resourcePackages);
            }
            if (((GenericOpenApiContext) ctx).getResourceClasses() == null && resourceClasses != null) {
                ((GenericOpenApiContext) ctx).resourceClasses(resourceClasses);
            }
            if (init) {
                ctx.init(); // includes registering itself with OpenApiContextLocator
            }
        }
        return ctx;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    public T servletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        return (T) this;
    }

}
