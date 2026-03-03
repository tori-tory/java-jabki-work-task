package ru.jabki.work.task.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestUserConfig {
    @Bean
    public RestClient restClientUser(){
        return RestClient.builder().baseUrl("http://localhost:8081/api/v1").build();
    }
}