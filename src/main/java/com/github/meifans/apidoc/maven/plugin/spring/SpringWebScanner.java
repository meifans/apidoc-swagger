package com.github.meifans.apidoc.maven.plugin.spring;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.integration.IgnoredPackages;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * @author pengfei.zhao
 */
public class SpringWebScanner implements OpenApiScanner {

    private static final Set<String> ignored = new HashSet<>();
    private static Logger LOGGER = LoggerFactory.getLogger(SpringWebScanner.class);

    static {
        ignored.addAll(IgnoredPackages.ignored);
    }

    private OpenAPIConfiguration openApiConfiguration;

    @Override
    public Set<Class<?>> classes() {

        if (openApiConfiguration == null) {
            openApiConfiguration = new SwaggerConfiguration();
        }

        ClassGraph graph = new ClassGraph().enableAllInfo();
        Set<String> acceptablePackages = new HashSet<String>();
        Set<Class<?>> output = new HashSet<Class<?>>();

        // if classes are passed, use them
        if (openApiConfiguration.getResourceClasses() != null && !openApiConfiguration.getResourceClasses().isEmpty()) {
            for (String className : openApiConfiguration.getResourceClasses()) {
                if (!isIgnored(className)) {
                    try {
                        output.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        LOGGER.warn("error loading class from resourceClasses: " + e.getMessage(), e);
                    }
                }
            }
            return output;
        }

        boolean allowAllPackages = false;
        if (openApiConfiguration.getResourcePackages() != null && !openApiConfiguration.getResourcePackages().isEmpty()) {
            for (String pkg : openApiConfiguration.getResourcePackages()) {
                if (!isIgnored(pkg)) {
                    acceptablePackages.add(pkg);
                    graph.whitelistPackages(pkg);
                }
            }
        } else {
            allowAllPackages = true;
        }
        final Set<Class<?>> classes;
        try (ScanResult scanResult = graph.scan()) {
            classes = new HashSet<>(scanResult.getClassesWithAnnotation(OpenAPIDefinition.class.getName()).loadClasses());
            for (String className : getSpringClassAnnotation()) {
                classes.addAll(scanResult.getClassesWithAnnotation(className).loadClasses());
            }
        }

        for (Class<?> cls : classes) {
            if (allowAllPackages) {
                output.add(cls);
            } else {
                for (String pkg : acceptablePackages) {
                    if (cls.getPackage().getName().startsWith(pkg)) {
                        output.add(cls);
                    }
                }
            }
        }
        LOGGER.trace("classes() - output size {}", output.size());
        return output;
    }

    public List<String> getSpringClassAnnotation() {
        List<String> classNames = new ArrayList<>();
        classNames.add(org.springframework.web.bind.annotation.RestController.class.getName());
        classNames.add(org.springframework.web.bind.annotation.RequestMapping.class.getName());

        return classNames;
    }

    @Override
    public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
        this.openApiConfiguration = openApiConfiguration;
    }

    @Override
    public Map<String, Object> resources() {
        return Collections.emptyMap();
    }

    private boolean isIgnored(String classOrPackageName) {
        if (StringUtils.isBlank(classOrPackageName)) {
            return true;
        }
        return ignored.stream().anyMatch(i -> classOrPackageName.startsWith(i));
    }
}
