package com.example.appointment.exceptions;

import com.example.appointment.ConnectionModel;
import javafx.scene.control.Alert;

/**
 * An exception that is designed to be thrown if a customer is being deleted that already has appointments associated with it.
 * */
public class CustomerHasAppointmentsException extends Exception{

    /**
     * A constructor that creates the CustomerHasAppointmentsException object.
     * */
    public CustomerHasAppointmentsException(){
        super();
    }

    /**
     * Displays an alert window with the text that displays an error message.
     * */
    public  void Alert(){
        ConnectionModel connectionModel = ConnectionModel.getInstance();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(connectionModel.getResourceBundle().getString("warningAlert"));
        alert.setHeaderText(connectionModel.getResourceBundle().getString("customerHasAppointmentsAlertText"));
        alert.showAndWait();
    }
}