package com.benchmark.rest.datarest.config;

import com.benchmark.rest.datarest.model.Category;
import com.benchmark.rest.datarest.model.Item;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Component
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config,
            CorsRegistry cors) {
        
        config.exposeIdsFor(Category.class, Item.class);
        config.setBasePath("/api");
    }
}

