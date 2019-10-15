package com.github.meifans.apidoc.maven.plugin.spring;

import com.github.meifans.apidoc.maven.plugin.OpenAPIBuilder;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * Support: `@RestController`、`@RequestMapping`
 *
 * @author meifans
 */
public class SpringWebReader implements OpenApiReader {

    protected OpenAPIConfiguration config;

    @Override
    public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
        this.config = openApiConfiguration;
    }

    @Override
    public OpenAPI read(Set<Class<?>> classes, Map<String, Object> resources) {
        OpenAPIBuilder builder = new OpenAPIBuilder();
        classes.stream()
               .filter(this::isSupportFor)
               .forEach(c -> readClass(c, builder));
        return builder.build();
    }

    private void readClass(Class<?> clazz, final OpenAPIBuilder builder) {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
        final String pathPrefix = getPathPrefix(requestMapping.path());

        Arrays.stream(clazz.getMethods())
              .filter(this::isSupportFor)
              .forEach(method -> {
                  RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class); // 打穿annotation 的继承和属性间的别名
                  String path = pathPrefix + getPathsuffix(mapping.path());
                  Operation operation = buildOperation(method, mapping);
                  builder.addOperation(path, operation, getHttpMethod(mapping.method()));
              });
    }

    private Operation buildOperation(Method method, RequestMapping mapping) {
        Operation operation = new Operation();

        int count = method.getParameterTypes().length;
        List<Parameter> parameters = IntStream.range(0, count)
                                              .mapToObj(i -> buildParameter(new MethodParameter(method, i)))
                                              .collect(Collectors.toList());
        operation.setParameters(parameters);
        return operation;
    }

    private Parameter buildParameter(MethodParameter methodParameter) {
        Parameter parameter = new Parameter();
        return parameter;
    }

    private RequestMethod getHttpMethod(RequestMethod[] methods) {
        return ObjectUtils.isEmpty(methods) ? RequestMethod.GET : methods[0];
    }

    private String getPathPrefix(String[] paths) {
        if (ObjectUtils.isEmpty(paths)) {
            return "/";
        }
        String path = paths[0];

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    private String getPathsuffix(String[] paths) {
        if (ObjectUtils.isEmpty(paths) || paths[0].equals("/")) {
            return "";
        }
        String path = paths[0];

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }


    private boolean isSupportFor(final Method method) {
        return Stream.of(RequestMapping.class, GetMapping.class, PutMapping.class, PostMapping.class, DeleteMapping.class, PatchMapping.class)
                     .anyMatch(annotation -> AnnotationUtils.findAnnotation(method, annotation) != null);
    }

    private boolean isSupportFor(Class<?> clazz) {
        return AnnotationUtils.isAnnotationDeclaredLocally(RequestMapping.class, clazz)
                || AnnotationUtils.isAnnotationDeclaredLocally(RestController.class, clazz);
    }

}
