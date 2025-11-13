package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    File sentFile;
    public Consumer<NtfyMessageDto> messageHandler;

    @Override
    public CompletableFuture<Boolean> send(String message) {
        this.message = message;
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Void> receive(Consumer<NtfyMessageDto> messageHandler) {
        this.messageHandler = messageHandler;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> sendFile(File file) {
        this.sentFile = file;
        if (file == null || !file.exists()) {
            return CompletableFuture.failedFuture(
                    new FileNotFoundException("File not found: " + file)
            );
        }
        return CompletableFuture.completedFuture(true);
    }

    public void simulateIncomingMessages(String message) {
        var dto = new NtfyMessageDto("id1", 0L, "message", "mytopic", message, null);
        messageHandler.accept(dto);

    }
}
