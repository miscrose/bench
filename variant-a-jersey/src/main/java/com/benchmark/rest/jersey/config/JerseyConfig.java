package com.benchmark.rest.jersey.config;

import com.benchmark.rest.jersey.resource.CategoryResource;
import com.benchmark.rest.jersey.resource.ItemResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(CategoryResource.class);
        register(ItemResource.class);
    }
}

