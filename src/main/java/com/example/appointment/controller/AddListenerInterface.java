package com.example.appointment.controller;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

/**
 * An interface that adds a listener to a passed in JavaFX object so that the label is displayed revealing an error message, and
 * the JavaFX object will change to red, until a valid text entry is entered.
 * */
public interface AddListenerInterface{

    /**
     * A method that makes a label visible that displays an error message if the text in a textfield is invalid.
     * It also changes the color of the passed in textfield. This is to alert the user that the text that was typed in was invalid and that they need to try again.
     * <em>Lambda Expressions</em>
     * The lambda expression allows the listener to do the following if any text is entered after the method is ran:
     * <br>
     *  <ol>
     *      <li>Make the label visible.</li>
     *      <li>Remove the style so the TextField is no longer red.</li>
     *  </ol>
     * @param textField Takes in a passed in TextField to check for valid text.
     * @param label Takes in a label to make visible if the text is invalid.
     * @param message Takes in text to set the label to. This is used to make it easier to use this method in different parts of the program.
     * */
    default void addListenerToLabel(TextField textField, Label label, String message){
        label.setText(message);
        label.setVisible(true);
        textField.setStyle("-fx-border-color: 'red'");
        textField.textProperty().addListener(
                (observable) -> {
                    label.setVisible(false);
                    textField.setStyle(null);
                }
        );
    }

    /**
     * A method that makes a label visible that displays an error message depending on the values into a spinner.
     * It also changes the color of the spinner to red in order to alert the user.
     * This is to display an error message if the values in the spinner are invalid.
     *  <em>Lambda Expressions</em>
     *  The lambda expression allows the listener to do the following if any text is entered after the method is ran:
     *  <br>
     *  <ol>
     *      <li>Make the label visible.</li>
     *      <li>Remove the style so the Spinner is no longer red.</li>
     *  </ol>
     * @param spinner Takes in a passed in Spinner to check for a valid entry
     * @param label Takes in a label to make visible if the text is invalid.
     * */
    default void addListenerToLabel(Spinner<Integer> spinner, Label label){
        label.setVisible(true);
        spinner.setStyle("-fx-border-color: 'red'");
        spinner.valueFactoryProperty().getValue().valueProperty().addListener(
                (observable) -> {
                    label.setVisible(false);
                    spinner.setStyle(null);
                }
        );
    }

    /**
     * A method that makes a label visible that displays an error message depending on the values into a date picker.
     * It also changes the color of the date picker to red in order to alert the user.
     * This is to display an error message if the values in the date picker are invalid.
     *  <em>Lambda Expressions</em>
     *  The lambda expression allows the listener to do the following if any text is entered after the method is ran:
     *  <br>
     *  <ol>
     *      <li>Make the label visible.</li>
     *      <li>Remove the style so the Date Picker is no longer red.</li>
     *  </ol>
     * @param datePicker Takes in a passed in date picker to check for a valid entry
     * @param label Takes in a label to make visible if the text is invalid.
     * */
    default void addListenerToLabel(DatePicker datePicker, Label label){
        label.setVisible(true);
        datePicker.setStyle("-fx-border-color: 'red'");
        datePicker.valueProperty().addListener(
                (observable) -> {
                    label.setVisible(false);
                    datePicker.setStyle(null);
                }
        );
    }
}
