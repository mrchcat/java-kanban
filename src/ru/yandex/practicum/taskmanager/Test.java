package ru.yandex.practicum.taskmanager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriToGetId = URI.create("http://localhost:8080/tasks/2sxax0");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        HttpResponse<String> response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());

    }
}
