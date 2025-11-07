package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel(new NtfyConnectionImpl());

    @FXML
    private Button fileChooser;

    @FXML
    private VBox messageBox;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextArea textArea;

    private boolean showingPlaceholder = true;

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
          model.receiveMessage();
        
        textArea.setOnKeyPressed(event -> {
           if (showingPlaceholder) {
               clearPlaceholder();
           }
        });
        textArea.setOnMouseClicked(event -> {
            if ( showingPlaceholder) {
                clearPlaceholder();
            }
        });

    }

    private void clearPlaceholder() {
        textArea.clear();
        textArea.setStyle("-fx-text-fill: black");
        showingPlaceholder = false;
    }

    private void setPlaceholder() {
        textArea.setText("Skriv ett meddelande...");
        textArea.setStyle("-fx-text-fill: gray;");
        showingPlaceholder = true;
    }

    public void sendMessage() {
        String message = textArea.getText();
        if (showingPlaceholder || message.isEmpty()) {
            return;
        }
        sentMessageBubble(message);
        model.setMessageToSend(message);
        model.sendMessage();
        textArea.clear();
        setPlaceholder();
    }

    private void sentMessageBubble(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        Label messageBubble = new Label(message);
        messageBubble.getStyleClass().add("chat-bubble-sent");
        msgBubbleFormatter(messageContainer, messageBubble);
    }

    private void receivedMessageBubble(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
        Label messageBubbleLeft = new Label(message);
        messageBubbleLeft.getStyleClass().add("chat-bubble-received");
        msgBubbleFormatter(messageContainer, messageBubbleLeft);
    }

    private void msgBubbleFormatter(HBox messageContainer, Label messageBubbleLeft) {
        messageBubbleLeft.setWrapText(true);
        messageBubbleLeft.setMaxWidth(300);
        messageBubbleLeft.setMinWidth(Label.USE_PREF_SIZE);
        messageBubbleLeft.setPrefWidth(Label.USE_COMPUTED_SIZE);
        HBox.setHgrow(messageContainer, Priority.ALWAYS);
        messageContainer.setMaxWidth(Double.MAX_VALUE);
        messageContainer.getChildren().add(messageBubbleLeft);
        messageBox.getChildren().add(messageContainer);
    }

    public void attachFile(ActionEvent actionEvent) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Välj fil att bifoga...");

        Window window = textArea.getScene().getWindow();
        var selectedFile = fc.showOpenDialog(window);

    }
}
