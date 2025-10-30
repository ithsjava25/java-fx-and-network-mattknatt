package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private StringProperty userInputProperty;

    public HelloModel() {
        userInputProperty = new SimpleStringProperty();
    }


    public String getUserInput() {
        return userInputProperty.get();
    }

    public StringProperty userInputProperty() {
        return userInputProperty;
    }

    public void setUserInput(String userInput) {
        userInputProperty.set(userInput);
    }


}
