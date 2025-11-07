package com.example;

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

    public void simulateIncomingMessages(String message) {
        var dto = new NtfyMessageDto("id1", 0L, "message", "mytopic", message);
        messageHandler.accept(dto);

    }
}
