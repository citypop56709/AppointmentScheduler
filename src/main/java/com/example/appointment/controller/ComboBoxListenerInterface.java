package com.example.appointment.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * An interface that adds a listener to a passed in ComboBox so that text that is entered can be validated.
 * */
public interface ComboBoxListenerInterface {
    /**
     * A method that validates text entered in to a ComboBox and verifies if the text entered in matches the items in the box.
     * If the text does not match then the ComboBox will be turned to red, and the value will be cleared out to alert the user
     * that it's an invalid entry. It verifies if the value is valid by checking if the passed in observable list contains the integer selected in the ComboBox.
     * @param comboBox Passes in a  ComboBox to be verified.
     * @param observableList A list of items to enter in as values for the ComboBox.
     * */
    default void setIntegerComboBoxValidation(ComboBox<Integer> comboBox, ObservableList<Integer> observableList){
        comboBox.setItems(observableList);
        comboBox.editorProperty().getValue().textProperty().addListener((observable, oldValue, newValue) ->{
            try{
                if (observableList.contains(Integer.parseInt(newValue))){
                    comboBox.setStyle(null);
                    comboBox.setValue(Integer.parseInt(newValue));
                }
                else{
                    throw new IllegalArgumentException();
                }
            }
            catch(IllegalArgumentException e){
                Platform.runLater(() ->{
                    comboBox.editorProperty().getValue().clear();
                });
                comboBox.setStyle("-fx-border-color: 'red'");
            }
        });
    }

    /**
     * A method that validates text entered in to a ComboBox and verifies if the text entered in matches the items in the box.
     * If the text does not match then the ComboBox will be turned to red, and the value will be cleared out to alert the user
     * that it's an invalid entry. It verifies if the value is valid by checking if the passed in observable list contains the string selected in the ComboBox.
     * @param comboBox Passes in a  ComboBox to be verified.
     * @param observableList A list of items to enter in as values for the ComboBox.
     * */
    default void setStringComboBoxValidation(ComboBox<String> comboBox, ObservableList<String> observableList){
        comboBox.setItems(observableList);
        comboBox.editorProperty().getValue().textProperty().addListener((observable, oldValue, newValue) ->{
            try{
                if (observableList.contains(newValue)){
                    comboBox.setStyle(null);
                    comboBox.setValue(newValue);
                }
                else{
                    throw new IllegalArgumentException();
                }
            }
            catch(IllegalArgumentException e){
                Platform.runLater(() ->
                        comboBox.editorProperty().getValue().clear()
                );
                comboBox.setStyle("-fx-border-color: 'red'");
            }
        });
    }

    /**
     * A method that makes a label visible that displays an error message if the combobox that is passed in doesn't have a value.
     * It also changes the color of the passed in combobox. This is to alert the user that the text that was typed in was invalid and that they need to try again.
     * <em>Lambda Expressions</em>
     * The lambda expression allows the listener to do the following if any text is entered after the method is ran:
     * <br>
     *  <ol>
     *      <li>Make the error label invisible.</li>
     *      <li>Remove the style so the ComboBox is no longer red.</li>
     *  </ol>
     * @param comboBox Takes in a passed in integer ComboBox whose values are checked.
     * @param label Takes in a label to make visible if the text is invalid.
     * */
    default void addListenerToComboBox(ComboBox<Integer> comboBox, Label label){
        label.setVisible(true);
        comboBox.setStyle("-fx-border-color: 'red'");
        comboBox.valueProperty().addListener(
            (observable) -> {
                label.setVisible(false);
                comboBox.setStyle(null);
            });
    }
}
