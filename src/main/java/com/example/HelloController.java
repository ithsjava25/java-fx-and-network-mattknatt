package com.example;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    private VBox chatMessages;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextArea messageInput;

    private boolean showingPlaceholder = true;

    public StringProperty userInputProperty() {
        return model.userInputProperty();
    }


    @FXML
    private void initialize() {

        setPlaceholder();
        
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

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();

        if (showingPlaceholder || message.isEmpty()) {
            return;
        }
        model.setUserInput(message);

        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        Label messageBubble = new Label(message);
        messageBubble.getStyleClass().add("chat-bubble");
        messageBubble.setWrapText(true);
        messageBubble.setMaxWidth(300);
        messageBubble.setMinWidth(Label.USE_PREF_SIZE);
        messageBubble.setPrefWidth(Label.USE_COMPUTED_SIZE);
        messageContainer.getChildren().add(messageBubble);
        chatMessages.getChildren().add(messageContainer);
        messageInput.clear();

    }
}
