package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NtfyMessageDto(String id, long time, String event, String topic, String message, Attachment attachment) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Attachment(String url, String name, String type){}
}


