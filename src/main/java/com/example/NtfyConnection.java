package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface NtfyConnection {

    public CompletableFuture<Boolean> send(String message);

    public CompletableFuture<Void> receive(Consumer<NtfyMessageDto> messageHandler);

    public CompletableFuture<Boolean> sendFile(File file) throws FileNotFoundException;
}
