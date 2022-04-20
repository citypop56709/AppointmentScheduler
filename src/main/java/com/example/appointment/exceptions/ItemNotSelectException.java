package com.example.appointment.exceptions;

import com.example.appointment.ConnectionModel;
import javafx.scene.control.Alert;

/**
 * An exception that is designed to be thrown if the modify item button is pressed without an item highlighted.
 * */
public class ItemNotSelectException extends Exception{

    /**
     * A message to be displayed in the alert content text
     * */
    String message;

    /**
     * A constructor that creates an ItemNotSelectException object.
     * @param message - Passes in the text to be saved as the alert content text.
     * */
    public ItemNotSelectException(String message){
        super(message);
        this.message = message;
    }

    /**
     * Displays an alert window with the text that displays an error message. The content text comes from the message member field.
     * */
    public  void Alert(){
        ConnectionModel connectionModel = ConnectionModel.getInstance();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(connectionModel.getResourceBundle().getString("warningAlert"));
        alert.setHeaderText(connectionModel.getResourceBundle().getString("itemNotSelectedHeaderText"));
        alert.setContentText(message);
        alert.showAndWait();
    }
}
