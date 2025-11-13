package com.example;


import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class HelloModelTest {

    @Test
    @DisplayName("Given a model with messageToSend when" +
            " calling sendMessage then should call send " +
            "method on connection")
    void sendMessageCallsConnectionWithMessageToSend() {
        //Arrange   Given
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);
        model.setUserName("");
        model.setMessageToSend("Hello World");
        //Act   When
        model.sendMessage();
        //Assert   Then
        assertThat(spy.message).isEqualTo("Hello World");

    }

    @Test
    void shouldStoreUserName() {
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);

        model.setUserName("user");

        assertThat(model.getUserName()).isEqualTo("user");
    }

    @Test
    void shouldDisplayUsernameInMessageIfPresent() {
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);
        model.setUserName("user");
        model.setMessageToSend("Hello World");

        model.sendMessage();

        assertThat(spy.message).isEqualTo("user: Hello World");

    }

    @Test
    void shouldNotDisplayMessagesSentBySelf() {
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);

        model.setUserName("user");
        model.setMessageToSend("Hello World");

        model.receiveMessage();

        spy.simulateIncomingMessages("user: Hello World");

        assertTrue(model.getMessages().isEmpty());

    }

    @Test
    void sendMessageToFakeServer(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wireMockRuntimeInfo.getHttpPort());
        var model = new HelloModel(con);
        model.setUserName("user");
        model.setMessageToSend("Hello World");
        stubFor(post("/mytopic")
                .willReturn(ok()));

        model.sendMessage().join();

        //Verify call made to server

        verify(postRequestedFor(urlEqualTo("/mytopic"))
                .withRequestBody(containing("user: Hello World")));
    }

    @Test
    void sendAttachmentToFakeServer(WireMockRuntimeInfo wireMockRuntimeInfo) {
        File testFile = new File("src/test/resources/test.txt");
        var con = new NtfyConnectionImpl("http://localhost:" + wireMockRuntimeInfo.getHttpPort());
        var model = new HelloModel(con);
        model.setAttachedFile(testFile);

        stubFor(put(urlEqualTo("/mytopic"))
                .willReturn(ok()));

        var result = model.sendFile().join();

        assertThat(result).isTrue();

        verify(putRequestedFor(urlEqualTo("/mytopic"))
                .withHeader("Filename", equalTo(testFile.getName())));

    }

    @Test
    void sendFileShouldThrowExceptionWhenFileDoesNotExist() {
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);
        File missingFile = new File("src/test/resources/missing-file.txt");
        model.setAttachedFile(missingFile);

        var future = model.sendFile();

        CompletionException ex = assertThrows(CompletionException.class, future::join);
        assertInstanceOf(FileNotFoundException.class, ex.getCause());
    }

    @Test
    void receiveMessageShouldStoreIncomingMessages() {
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);

        spy.receive(m -> model.getMessages().add(m));

        spy.simulateIncomingMessages("Hello World");

        assertThat(model.getMessages()).hasSize(1);
        assertThat(model.getMessages().getFirst().message()).isEqualTo("Hello World");


    }

    @Test
    void receiveMessageFromFakeServer(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wireMockRuntimeInfo.getHttpPort());
        var model = new HelloModel(con);

        String jsonMessage = """
                {"id":"abc123","time":17000000,"event":"message","topic":"mytopic","message":"Hello World"}
                """;

        stubFor(get(urlEqualTo("/mytopic/json"))
                .willReturn(okForContentType("application/json", jsonMessage)));

        model.receiveMessage().join();


        assertThat(model.getMessages()).hasSize(1);
        var dto = model.getMessages().getFirst();
        assertThat(dto.message()).isEqualTo("Hello World");

        verify(getRequestedFor(urlEqualTo("/mytopic/json")));
    }

    @Test
    void receiveFileShouldStoreAttachmentMessage() {
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);

        var attachment = new NtfyMessageDto.Attachment(
                "https://example.com/files/test.jpg",
                "test.jpg",
                "image/jpeg"
        );

        var dto = new NtfyMessageDto("id1", 10101010L, "message", "mytopic", "File received", attachment);

        model.receiveMessage();
        spy.messageHandler.accept(dto);

        assertThat(model.getMessages()).hasSize(1);
        var received = model.getMessages().getFirst();
        assertThat(received.attachment()).isNotNull();
        assertThat(received.attachment().url()).isEqualTo("https://example.com/files/test.jpg");
        assertThat(received.attachment().name()).isEqualTo("test.jpg");
    }
}