package com.benchmark.rest.jersey;

import jakarta.ws.rs.ApplicationPath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@ApplicationPath("/api")
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class JerseyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JerseyApplication.class, args);
    }
}

