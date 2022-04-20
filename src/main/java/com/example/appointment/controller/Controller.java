package com.example.appointment.controller;

import com.example.appointment.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

/**
 * A base class shared by all the controllers. Provides methods to change scenes that most controllers use in order to reduce code reuse.
 * */
public class Controller implements SQLControllerInterface{
    /**
     * Changes scenes back to the Login page whenever the Logout button is pressed.
     * @param event Takes in that the Logout button was pressed.
     * */
    @FXML
    public void changeSceneToLoginController(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("login-page.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((LoginController) loader.getController()).setDefaultControllerLanguage();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("headerTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningAlert"));
            alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("warningAlertIOHeaderText"));
            alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("warningAlertIOContextText"));
            alert.showAndWait();
            Platform.exit();
        }
    }

    /**
     * Changes scene to the main page whenever the cancel button is pressed, or if an item is saved to the database.
     * @param event Takes in that a button was pressed.
     * */
    @FXML
    public void changeSceneToMainController(ActionEvent event){
        try {
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
        }
        catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningAlert"));
            alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("warningAlertIOHeaderText"));
            alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("warningAlertIOContextText"));
            alert.showAndWait();
            Platform.exit();
        }
    }

    /**
     * A method that changes the scene to the view customers page when the view customers radio button is pressed.
     * The view customers page loads its own TableView with the customer data, and aligns directly with the appointment table view.
     * @param event Takes in that the View Customers radio button was pressed.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException If the database error occurs.
     * */
    @FXML
    public void changeSceneToViewCustomersController(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view-customers.fxml"), CONNECTION_MODEL.getResourceBundle());
        Parent root = loader.load();
        ((ViewCustomersController) loader.getController()).setCustomerTableView();
        ((ViewCustomersController) loader.getController()).setCustomersIdsWithAppointments();
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("mainMenuHeaderTextLabel"));
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
}
