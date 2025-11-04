package com.example;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

//    @FXML
//    private ListView<NtfyMessageDto> messageView;

    @FXML
    private VBox chatMessages;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextArea messageInput;

    private boolean showingPlaceholder = true;

//    public StringProperty userInputProperty() {
//        return model.userInputProperty();
//    }


    @FXML
    private void initialize() {

        setPlaceholder();

        model.getMessages().addListener((javafx.collections.ListChangeListener<NtfyMessageDto>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var msg : change.getAddedSubList()) {
                        receivedMessageBubble(msg.message());
                    }
                }
            }
        });
        
        messageInput.setOnKeyPressed(event -> {
           if (showingPlaceholder) {
               clearPlaceholder();
           }
        });
        messageInput.setOnMouseClicked(event -> {
            if ( showingPlaceholder) {
                clearPlaceholder();
            }
        });


    }

    private void clearPlaceholder() {
        messageInput.clear();
        messageInput.setStyle("-fx-text-fill: black");
        showingPlaceholder = false;
    }

    private void setPlaceholder() {
        messageInput.setText("Skriv ett meddelande...");
        messageInput.setStyle("-fx-text-fill: gray;");
        showingPlaceholder = true;
    }

    public void sendMessage() {
        String message = messageInput.getText();
        model.sendMessage(message);
        if (showingPlaceholder || message.isEmpty()) {
            return;
        }
        model.setUserInput(message);
        sentMessageBubble(message);
        messageInput.clear();
        setPlaceholder();
    }

//    public void receiveMessage() {
//        model.receiveMessage();
//        String receivedMessage = model.getMessages().getLast().message();
//        if(receivedMessage.isEmpty()) {
//            return;
//        }
//        receivedMessageBubble(receivedMessage);
//    }

    private void sentMessageBubble(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        Label messageBubble = new Label(message);
        messageBubble.getStyleClass().add("chat-bubble-sent");
        messageBubble.setWrapText(true);
        messageBubble.setMaxWidth(300);
        messageBubble.setMinWidth(Label.USE_PREF_SIZE);
        messageBubble.setPrefWidth(Label.USE_COMPUTED_SIZE);
        HBox.setHgrow(messageContainer, Priority.ALWAYS);
        messageContainer.setMaxWidth(Double.MAX_VALUE);
        messageContainer.getChildren().add(messageBubble);
        chatMessages.getChildren().add(messageContainer);
    }

    private void receivedMessageBubble(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
        Label messageBubbleLeft = new Label(message);
        messageBubbleLeft.getStyleClass().add("chat-bubble-received");
        messageBubbleLeft.setWrapText(true);
        messageBubbleLeft.setMaxWidth(300);
        messageBubbleLeft.setMinWidth(Label.USE_PREF_SIZE);
        messageBubbleLeft.setPrefWidth(Label.USE_COMPUTED_SIZE);
        HBox.setHgrow(messageContainer, Priority.ALWAYS);
        messageContainer.setMaxWidth(Double.MAX_VALUE);
        messageContainer.getChildren().add(messageBubbleLeft);
        chatMessages.getChildren().add(messageContainer);
    }

    public void startConversation(ActionEvent actionEvent) {
        sendMessage();
    }
}
