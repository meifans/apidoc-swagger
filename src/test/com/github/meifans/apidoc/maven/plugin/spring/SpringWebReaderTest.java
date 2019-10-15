package com.github.meifans.apidoc.maven.plugin.spring;

import com.google.common.collect.Sets;

import com.github.meifans.apidoc.maven.plugin.spring.raw.PetController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import io.swagger.v3.oas.models.OpenAPI;

import static org.junit.Assert.*;

/**
 * @author meifans
 */
@RunWith(MockitoJUnitRunner.class) // https://static.javadoc.io/org.mockito/mockito-core/3.0.0/org/mockito/Mockito.html#23
public class SpringWebReaderTest {

    @InjectMocks
    SpringWebReader tester;

    //    @Mock

    @Test
    public void read() {
        Set<Class<?>> classes = Sets.newHashSet(PetController.class);

        OpenAPI api = tester.read(classes, null);

        assertNotNull(api);
        assertNotNull(api.getPaths());
        assertTrue(api.getPaths().containsKey("/api/pet/{pet_id}"));
    }
}