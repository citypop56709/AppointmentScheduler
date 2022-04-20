package com.example.appointment;

import com.example.appointment.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The main class that starts the appointment program and opens the login controller.
 * */
public class Main extends Application {
    /** This method sets the stage for the home page of the application.
     @param stage The primary stage for the application where the main scene can be set.
     @throws IOException If an object that is being called cannot be found.
     */
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        ConnectionModel connectionModel = ConnectionModel.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-page.fxml"), connectionModel.getResourceBundle());
        Parent root = loader.load();
        ((LoginController)loader.getController()).setDefaultControllerLanguage();
        Scene scene = new Scene(root);
        stage.setTitle(connectionModel.getResourceBundle().getString("headerTextLabel"));
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Main method that launches the appointment program.
     * @param args Takes in command-line arguments.
     * */
    public static void main(String[] args) {
        launch(args);
    }
}