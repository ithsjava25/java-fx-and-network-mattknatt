package com.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.concurrent.CompletableFuture;


/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {


    private File attachedFile;

    private final NtfyConnection connection;

    private final ObservableList<NtfyMessageDto> messages = FXCollections.observableArrayList();

    private final StringProperty messageToSend = new SimpleStringProperty();

    private final StringProperty userName = new SimpleStringProperty();

    public HelloModel(NtfyConnection connection) {
        this.connection = connection;
    }

    public CompletableFuture<Boolean> sendMessage() {
        return connection.send(getMessageToSend());
    }

    public CompletableFuture<Boolean> sendFile() {
        if (attachedFile == null) {
            return CompletableFuture.completedFuture(false);
        }
        return connection.sendFile(getAttachedFile());
    }

    public void setUserName(String userName) {
        this.userName.setValue(userName);
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public CompletableFuture<Void> receiveMessage() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        connection.receive(m -> {
            try {
                if(m.message() != null && m.message().startsWith(getUserName() + ": ")) {
                    return;
                }

                Runnable addTask = () -> {
                    messages.add(m);
                    if (!future.isDone()) {
                        future.complete(null);
                    }

                };

                if (Platform.isFxApplicationThread()) {
                    addTask.run();
                } else {
                    try {
                        Platform.runLater(addTask);
                    } catch (IllegalStateException e) {
                        addTask.run();
                    }
                }

            } catch (Exception e) {
                if (!future.isDone()) {
                    future.completeExceptionally(e);
                }
            }
        }).exceptionally(e -> {
            if (!future.isDone()) {
                future.completeExceptionally(e);
            }
            return null;
        });

        return future;

    }



    public ObservableList<NtfyMessageDto> getMessages() {
        return messages;
    }

    public String getMessageToSend() {
       String currentUser = getUserName();
       if (currentUser == null || currentUser.isBlank()) {
           return messageToSend.get();
       }
       return currentUser + ": " + messageToSend.get();
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

    public void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            System.out.println("Failed to open url: " + url);
        }
    }
}

