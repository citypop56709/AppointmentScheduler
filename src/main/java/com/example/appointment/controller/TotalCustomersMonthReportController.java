package com.example.appointment.controller;

import com.example.appointment.Customer;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A controller that loads the Total Customer Month Reports scene. This scene enables the user to select a type and see the total number of customers for that month.
 * */
public class TotalCustomersMonthReportController extends TotalCustomersTypeReportController{
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;
    @FXML
    private TableColumn<Customer, String> customerAddressColumn;
    @FXML
    private TableColumn<Customer, String> customerPostalCodeColumn;
    @FXML
    private TableColumn<Customer, String> customerPhoneColumn;
    @FXML
    private TableColumn<Customer, String> customerCreatedDateColumn;
    @FXML
    private TableColumn<Customer, String> customerCreatedByColumn;
    @FXML
    private TableColumn<Customer, String> customerLastUpdateColumn;
    @FXML
    private TableColumn<Customer, String> customerLastUpdatedByColumn;
    @FXML
    private TableColumn<Customer, String> customerDivisionColumn;
    @FXML
    private ComboBox<String> appointmentMonthComboBox;

    private PreparedStatement stmt;

    /**
     * A method that sets up the Month ComboBox using a DateFormatSymbols using object in order to ensure the list is internationalized.
     * It removes the 12th value from the list because the dateFormatSymbols object would always return a blank space for Calendar.UNDECIMBER value.
     * */
    @FXML
    public void setAppointmentMonthComboBox(){
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(CONNECTION_MODEL.getCurrentLocale());
        ArrayList<String> months = new ArrayList<>(Arrays.asList(dateFormatSymbols.getMonths()));
        months.remove(12);
        ObservableList<String> observableMonths = FXCollections.observableList(months);
        appointmentMonthComboBox.setItems(observableMonths);
    }

    /**
     * This method overrides the original method so that it returns a table view based on the month instead of the appointment type.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @see TotalCustomersTypeReportController#setCustomerTableView(ActionEvent)
     * */
    @FXML
    @Override
    public void setCustomerTableView(ActionEvent event) throws SQLConnectionDroppedException {
        try {
            Integer month = null;
            if ((event.getSource() instanceof ComboBox)){
                month = ((ComboBox<?>) event.getSource()).getItems().indexOf(((ComboBox<?>) event.getSource()).getValue());
            }
            String query = "SELECT * FROM CUSTOMERS WHERE Customer_ID IN (SELECT Customer_ID FROM APPOINTMENTS WHERE User_ID = ? AND month(Start) = ? AND year(Start) = ?)";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
            stmt.setInt(1, CONNECTION_MODEL.getUserId());
            stmt.setInt(2, month + 1);
            stmt.setInt(3, CONNECTION_MODEL.getCurrentTime().getYear());
            SortedList<Customer> sortedCustomerList = new SortedList<>(getCustomerList(stmt));
            sortedCustomerList.comparatorProperty().bind(customerTableView.comparatorProperty());
            customerDivisions = setCustomerDivisionNameList();
            customerTableView.setItems(sortedCustomerList);
            customerIdColumn.setCellValueFactory(
                    new PropertyValueFactory<>("customerID")
            );
            customerNameColumn.setCellValueFactory(
                    new PropertyValueFactory<>("customerName")
            );
            customerAddressColumn.setCellValueFactory(
                    new PropertyValueFactory<>("address")
            );
            customerPostalCodeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("postalCode")
            );
            customerPhoneColumn.setCellValueFactory(
                    new PropertyValueFactory<>("phone")
            );
            customerCreatedDateColumn.setCellValueFactory(
                    customer -> new SimpleStringProperty(customer.getValue().getLastUpdate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy", CONNECTION_MODEL.getCurrentLocale())))
            );
            customerCreatedByColumn.setCellValueFactory(
                    new PropertyValueFactory<>("createdBy")
            );
            customerLastUpdateColumn.setCellValueFactory(
                    customer -> new SimpleStringProperty(customer.getValue().getCreateDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy", CONNECTION_MODEL.getCurrentLocale())))
            );
            customerLastUpdatedByColumn.setCellValueFactory(
                    new PropertyValueFactory<>("lastUpdatedBy")
            );
            customerDivisionColumn.setCellValueFactory(
                    customer -> new SimpleStringProperty(customerDivisions.get(customer.getValue().getDivisionID()))
            );

            customerTableView.setPlaceholder(new Label(CONNECTION_MODEL.getResourceBundle().getString("customerTableSetAppointmentsPlaceHolderText")));
            TableView.TableViewSelectionModel<Customer> selectionCustomerModel = customerTableView.getSelectionModel();
            selectionCustomerModel.setSelectionMode(SelectionMode.SINGLE);
            setTotalCustomersLabel(sortedCustomerList.size());
        } catch (IOException |SQLException e) {
            CONNECTION_MODEL.SQLAlert();;
        }
        finally{
            closeSQLObjects(stmt);
        }
    }
}
