package ru.jabki.work.task.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Qualifier("restClientUser") RestClient restClient) {
        this.restClient = restClient;
    }

    public boolean existsById(Long userId) {
        return restClient.get()
                .uri("/user/exists/{id}", userId)
                .retrieve()
                .body(Boolean.class);
    }
}