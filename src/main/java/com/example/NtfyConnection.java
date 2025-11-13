package com.example;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface NtfyConnection {

     CompletableFuture<Boolean> send(String message);

     CompletableFuture<Void> receive(Consumer<NtfyMessageDto> messageHandler);

     CompletableFuture<Boolean> sendFile(File file);
}
