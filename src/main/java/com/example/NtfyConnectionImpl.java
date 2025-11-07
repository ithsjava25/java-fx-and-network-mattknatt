package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionImpl implements NtfyConnection {

    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;
    private final ObjectMapper mapper = new ObjectMapper();

    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public CompletableFuture<Boolean> send(String message) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/mytopic"))
                .build();


            //Todo: handle long blocking send requests to not freeze the JavaFX thread
            //1. Use thread send message?
            //2. Use async?
            return http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int code = response.statusCode();
                        return code == 200 || code == 201;
                    })
                    .exceptionally(e -> {
                        System.err.println("Send failed: " + e.getMessage());
                        return false;
                    });
    }

    @Override
    public CompletableFuture<Boolean> sendFile(File file) throws FileNotFoundException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                .uri(URI.create(hostName + "/mytopic"))
                .header("Filename", file.getName())
                .build();

        return http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200);

    }

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/mytopic/json?since=10m"))
                .build();

        http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> response.body()
                        .map(s ->
                                mapper.readValue(s, NtfyMessageDto.class))
                        .filter(message -> message.event().equals("message"))
                        .peek(System.out::println)
                        .forEach(messageHandler));
    }

}

