package com.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;


/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {


    private File attachedFile;

    private final NtfyConnection connection;

    private final ObservableList<NtfyMessageDto> messages = FXCollections.observableArrayList();

    private final StringProperty messageToSend = new SimpleStringProperty();

    public HelloModel(NtfyConnection connection) {
        this.connection = connection;
//        receiveMessage();
    }

    public CompletableFuture<Boolean> sendMessage() {
        return connection.send(messageToSend.get());
    }

    public CompletableFuture<Boolean> sendFile() throws FileNotFoundException {
        return connection.sendFile(getAttachedFile());
    }


    public void receiveMessage() {
        connection.receive(m ->
                Platform.runLater(() -> messages.add(m)));
    }


    public ObservableList<NtfyMessageDto> getMessages() {
        return messages;
    }

    public String getMessageToSend() {
        return messageToSend.get();
    }

    public StringProperty messageToSendProperty() {
        return messageToSend;
    }

    public void setMessageToSend(String messageToSend) {
        this.messageToSend.set(messageToSend);
    }

    public File getAttachedFile() {
        return attachedFile;
    }

    public void setAttachedFile(File attachedFile) {
        this.attachedFile = attachedFile;
    }
}

