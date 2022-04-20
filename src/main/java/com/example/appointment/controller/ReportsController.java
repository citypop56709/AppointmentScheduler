package com.example.appointment.controller;

import com.example.appointment.Main;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

/**
 * A base class shared by all the report controllers. Provides methods to change scenes that most controllers use in order to reduce code reuse.
 * */
public class ReportsController extends Controller{

    /**
     * Changes to the reports customer schedule scene when the reports button is pressed.
     * @param event Takes in that the appropriate button was pressed.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException If the database error occurs.
     * */
    @FXML
    public void changeReportsCustomerScheduleScene(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("contact-schedule-report.fxml"), CONNECTION_MODEL.getResourceBundle());
        Parent root = loader.load();
        ((ContactScheduleReportController) loader.getController()).setContactIdComboBox();
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("reportsHeaderTextLabel"));
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }

    /**
     * Changes to the total customers by type reports scene when the appropriate button is pressed.
     * @param event Takes in that the appropriate button was pressed.
     * @throws IOException If an input/output error occurs.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void changeTotalCustomersByTypeReportsScene(ActionEvent event) throws IOException, SQLConnectionDroppedException{
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("total-customers-by-type-report.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((TotalCustomersTypeReportController) loader.getController()).setTypeComboBox();
            ((TotalCustomersTypeReportController) loader.getController()).setCustomerDivisionNameList();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("reportsHeaderTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (SQLConnectionDroppedException | IOException e){
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * Changes to the total customers by month reports scene when the appropriate button is pressed.
     * @param event Takes in that the appropriate button was pressed.
     * @throws IOException If an input/output error occurs.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void changeTotalCustomersMonthReportsScene(ActionEvent event) throws IOException, SQLConnectionDroppedException {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("total-customers-by-month-report.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((TotalCustomersMonthReportController) loader.getController()).setCustomerDivisionNameList();
            ((TotalCustomersMonthReportController) loader.getController()).setAppointmentMonthComboBox();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("reportsHeaderTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (SQLConnectionDroppedException e){
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * Changes to the login data reports scene when the appropriate button.
     * @param event Takes in that the appropriate button was pressed.
     * @throws IOException If an input/output error occurs.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void changeTotalCustomersCountryReportsScene(ActionEvent event) throws IOException, SQLConnectionDroppedException {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("total-customers-by-country-report.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((TotalCustomersCountryReportController) loader.getController()).setCustomerComboBoxModel();
            ((TotalCustomersCountryReportController) loader.getController()).setCustomerCountryComboBox();
            ((TotalCustomersCountryReportController) loader.getController()).setCustomerDivisionNameList();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("reportsHeaderTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (SQLException e){
            CONNECTION_MODEL.SQLAlert();
        }
    }
}
