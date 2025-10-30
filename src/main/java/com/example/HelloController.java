package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    private TextArea messageInput;

    private boolean showingPlaceholder = true;

    @FXML
    private Label messageLabel;

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



//        if (messageLabel != null) {
//            messageLabel.setText(model.getGreeting());
//        }
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
        String userInput = messageInput.getText();

        messageInput.clear();


    }
}
