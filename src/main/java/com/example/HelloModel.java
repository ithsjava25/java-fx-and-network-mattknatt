package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private final String hostName;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final ObservableList<NtfyMessageDto> messages = FXCollections.observableArrayList();

    private StringProperty userInputProperty;

    public HelloModel() {
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
        userInputProperty = new SimpleStringProperty();
        receiveMessage();
    }

    public void sendMessage(String message) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/mytopic"))
                .build();

        try {
            //Todo: handle long blocking send requests to not freeze the JavaFX thread
            //1. Use thread send message?
            //2. Use async?
            var response = http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.out.println("Error sending message");
        } catch (InterruptedException e) {
            System.out.println("Interrupted sending message");
        }

    }

    public void receiveMessage() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/mytopic/json?since=10m"))
                .build();

        http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response-> response.body()
                        .map(s->
                                mapper.readValue(s, NtfyMessageDto.class))
                        .filter(message->message.event().equals("message"))
                        .peek(System.out::println)
                        .forEach(s->
                                Platform.runLater(()-> messages.add(s))));
    }

    public ObservableList<NtfyMessageDto> getMessages() {
        return messages;
    }

    public String getUserInput() {
        return userInputProperty.get();
    }

    public StringProperty userInputProperty() {
        return userInputProperty;
    }

    public void setUserInput(String userInput) {
        userInputProperty.set(userInput);
    }

}
