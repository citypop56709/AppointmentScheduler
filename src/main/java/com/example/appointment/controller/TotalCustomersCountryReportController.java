package com.example.appointment.controller;

import com.example.appointment.Customer;
import com.example.appointment.CustomerComboBoxModel;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;

/**
 * A controller that loads the Total Customer Country Reports scene. This scene enables the user to select a country and see the total number of customers that are in that country.
 * */
public class TotalCustomersCountryReportController extends TotalCustomersMonthReportController{
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
    private ComboBox<String> customerCountryComboBox;

    private PreparedStatement stmt;

    private CustomerComboBoxModel customerComboBoxModel;

    /**
     * Sets the model instance for this controller so that it's using the same model throughout the program.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    public void setCustomerComboBoxModel() throws SQLException, IOException {
        customerComboBoxModel = CustomerComboBoxModel.getInstance(CONNECTION_MODEL);
    }


    /**
     * A method that loads the customer country ComboBox with values from the database.
     * After the ComboBox gets it values from the setComboBoxValidation method it sorts the values in numerical order.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    public void setCustomerCountryComboBox() throws SQLException, IOException{
        HashMap<String, Integer> reverseCountryHashMap = new HashMap<>();
        ObservableList<String> observableCountries = customerCountryComboBox.getItems();
        customerComboBoxModel.getCountriesComboBoxHashMap().forEach((key, value) -> {
            observableCountries.add(value);
            reverseCountryHashMap.put(value, key);
        });
        Collections.sort(observableCountries);
        customerCountryComboBox.setOnAction(actionEvent -> {
            Integer countryId = reverseCountryHashMap.get(customerCountryComboBox.getValue());
            try {
                setCustomerTableView(countryId);
            } catch (SQLConnectionDroppedException e) {
                try {
                    CONNECTION_MODEL.SQLAlert();
                } catch (SQLConnectionDroppedException ex) {
                    changeSceneToLoginController(actionEvent);
                }
            }
        });
    }

    /**
     * A method that retrieves a list of customers from the database based on the country ID and displays them in a Table View.
     * @param countryId Takes in the ID of the country that was passed in.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setCustomerTableView(Integer countryId) throws SQLConnectionDroppedException {
        try {
            String query = "SELECT * FROM customers WHERE Division_ID IN(select Division_ID FROM first_level_divisions WHERE Country_ID = ?); ";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
            stmt.setInt(1, countryId);
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
        } catch (IOException | SQLException e) {
            CONNECTION_MODEL.SQLAlert();;
        }
        finally{
            closeSQLObjects(stmt);
        }
    }
}
