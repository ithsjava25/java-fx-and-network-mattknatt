package com.example;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private Label messageLabel;

    @FXML
    private TextArea messageInput;

    private boolean showingPlaceholder = true;

    public StringProperty userInputProperty() {
        return model.userInputProperty();
    }


    @FXML
    private void initialize() {
        messageLabel.textProperty().bind(userInputProperty());

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
        model.setUserInput(messageInput.getText());
        if (showingPlaceholder || messageInput.getText().isEmpty()) {
            return;
        }
        model.setUserInput(messageInput.getText());

        messageInput.clear();

    }
}
