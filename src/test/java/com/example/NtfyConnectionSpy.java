package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    public Consumer<NtfyMessageDto> messageHandler;

    @Override
    public CompletableFuture<Boolean> send(String message) {
        this.message = message;
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler) {
        this.messageHandler = messageHandler;

    }

    @Override
    public CompletableFuture<Boolean> sendFile(File file) throws FileNotFoundException {
        return null;
    }

    public void simulateIncomingMessages(String message) {
        var dto = new NtfyMessageDto("id1", 0L, "message", "mytopic", message);
        messageHandler.accept(dto);

    }
}
