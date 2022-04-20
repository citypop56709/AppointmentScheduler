package com.example.appointment.controller;

import com.example.appointment.exceptions.SQLConnectionDroppedException;
import com.mysql.cj.log.Log;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Controller that provides the user with a page to login and change their language at. Includes exception handling to account for blank,
 * or incorrect usernames and passwords.
 * */
public class LoginController extends Controller implements AddListenerInterface{
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label usernameTextFieldLabel;
    @FXML
    private Label passwordTextFieldLabel;
    @FXML
    private Label currentZoneIdLabel;
    @FXML
    private ChoiceBox<String> languageChoiceBox;
    private String username;

    /**
     * This is a method that calls all the language translations methods to make it simpler to translate everything in the application.
     * */
    @FXML
    public void setDefaultControllerLanguage() {
        setLanguageChoiceBox(CONNECTION_MODEL.getCurrentLocale());
        setZoneIdLabel();
    }

    /**
     * Changes the Time Zone label to the user's current ZoneID.
     * */
    @FXML
    public void setZoneIdLabel(){
        currentZoneIdLabel.setText(ZoneId.systemDefault().toString());
    }

    /**
     * Sets up the language choice box so that the user can pick their language using it.
     * The method works by first getting the current language of the users OS, then it
     * sets the choice box to that setting and translates the login page to the user's setting.
     * It then sets up the setOnAction method so that a user can change the language if they wish.
     * @param userLocal The current locale of the user.
     * */
    @FXML
    public void setLanguageChoiceBox(Locale userLocal){
        languageChoiceBox.getItems().add("English");
        languageChoiceBox.getItems().add("Français");
        if (userLocal.getLanguage().equals("fr")){
            languageChoiceBox.setValue("Français");
        }
        else{
            languageChoiceBox.setValue("English");
        }
        languageChoiceBox.setOnAction(this::setControllerResourceBundle);
    }

    /**
     * This method changes the Resource Bundle for the Login Controller by changing the locale and then reloading the stage.
     * It changes the locale to match what the user selects from the choice box and resets the stage and stage title using that information.
     * @param event Takes in that a Language from the choice box was selected.
     * */
    @FXML
    public void setControllerResourceBundle(ActionEvent event){
        try {
            if (event.getSource() instanceof ChoiceBox) {
                String text = (String) ((ChoiceBox) event.getSource()).getValue();
                if (text.equals("English")) {
                    CONNECTION_MODEL.setCurrentLocale(Locale.ENGLISH);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appointment/login-page.fxml"),
                            CONNECTION_MODEL.getResourceBundle());
                    ((Node)event.getSource()).getScene().getWindow().hide();
                    Parent root = loader.load();
                    ((LoginController)loader.getController()).setLanguageChoiceBox(Locale.ENGLISH);
                    ((LoginController) loader.getController()).setZoneIdLabel();
                    Stage stage = new Stage();
                    stage.setTitle(CONNECTION_MODEL.getResourceBundle().getString("headerTextLabel"));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
                else if (text.equals("Français")) {
                    CONNECTION_MODEL.setCurrentLocale(Locale.FRANCE);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appointment/login-page.fxml"),
                        CONNECTION_MODEL.getResourceBundle());
                    ((Node)event.getSource()).getScene().getWindow().hide();
                    Parent root = loader.load();
                    ((LoginController) loader.getController()).setLanguageChoiceBox(Locale.FRANCE);
                    ((LoginController) loader.getController()).setZoneIdLabel();
                    Stage stage = new Stage();
                    stage.setTitle(CONNECTION_MODEL.getResourceBundle().getString("headerTextLabel"));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
            }
        }
        catch (IOException e) {
            String IOExceptionMessage = CONNECTION_MODEL.getResourceBundle().getString("IOExceptionMessage");
            addListenerToLabel(usernameTextField,usernameTextFieldLabel, IOExceptionMessage);
        }
    }

    /**
     * Clears the username and password TextFields, and clears the warning labels.
     * The method unbinds the text properties from both the labels until the user fails to login again.
     * */
    @FXML
    private void clearField(){
        usernameTextField.clear();
        passwordTextField.clear();
    }

    /**
     * Checks the login credentials entered by the user. Returns an error message if the username or password is invalid.
     * @return true or false depending on if the login credentials are valid.
     * */
    @FXML
    private boolean loginCheck(){
        boolean errorFlag = false;
        try {
            username = usernameTextField.getText();
            if (username.isEmpty()){
                addListenerToLabel(usernameTextField ,usernameTextFieldLabel , CONNECTION_MODEL.getResourceBundle().getString("usernameEmptyMessage"));
                errorFlag = true;
            }
            char[] password = passwordTextField.getText().toCharArray();
            if (passwordTextField.getText().isEmpty()){
                addListenerToLabel(passwordTextField, passwordTextFieldLabel, CONNECTION_MODEL.getResourceBundle().getString("passwordEmptyMessage"));
                errorFlag = true;
            }
            if (errorFlag){
                return false;
            }
            CONNECTION_MODEL.establishConnection(username, password);
            password = null;
        }
        catch (SQLException e){
            addListenerToLabel(usernameTextField, usernameTextFieldLabel, CONNECTION_MODEL.getResourceBundle().getString("SQLErrorMessage"));
            return false;
        }
        return true;
    }

    /**
     * Changes the scene to the main controller page to display the user's appointments.
     * This method is overridden because it has to make the login credentials are valid, and it has to display an alert
     * if an appointment is within 15 minute of login.
     * @param event Takes in that the login button was pressed.
     * */
    @FXML
    @Override
    public void changeSceneToMainController(ActionEvent event){
        try {
            boolean isLoginCheckValid = loginCheck();
            if (isLoginCheckValid) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appointment/main-menu.fxml"), CONNECTION_MODEL.getResourceBundle());
                Parent root = loader.load();
                ((MainController) loader.getController()).changeViewAll(event);
                ((MainController) loader.getController()).setZoneIdLabel();
                Scene scene = new Scene(root);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("mainMenuHeaderTextLabel"));
                window.setScene(scene);
                window.centerOnScreen();
                window.show();
                ((MainController) loader.getController()).appointmentAlert(((MainController) loader.getController()).getCurrentUserAppointmentsList());
            }
        }
        catch (IOException e){
            try{
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
    }
}