package com.example.appointment.controller;

import com.example.appointment.ConnectionModel;
import com.example.appointment.Customer;
import com.example.appointment.CustomerComboBoxModel;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * A controller that loads the Modify Customer page allowing users to modify the data of existing customers to the database.
 * */
public class ModifyCustomerController extends AddCustomerController implements PhoneNumberCheckInterface, PostalCodeCheckInterface, AddressCheckInterface{
    @FXML
    private TextField customerIdTextField;
    @FXML
    private TextField customerNameTextField;
    @FXML
    private TextField customerAddressTextField;
    @FXML
    private TextField customerPostalCodeTextField;
    @FXML
    private TextField customerPhoneTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private ComboBox<String> divisionComboBox;
    @FXML
    private Label customerNameErrorLabel;
    @FXML
    private Label customerAddressErrorLabel;
    @FXML
    private Label customerPostalCodeErrorLabel;
    @FXML
    private Label customerPhoneErrorLabel;
    private Customer selectedCustomer;

    /**
     * A Singleton instance that manages the customer data from the database.
     * */
    private CustomerComboBoxModel customerComboBoxModel;

    /**
     * Sets the model instance for this controller so that it's using the same model throughout the program.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @Override
    public void setCustomerComboBoxModel() throws SQLException, IOException {
        customerComboBoxModel = CustomerComboBoxModel.getInstance(CONNECTION_MODEL);
    }

    /**
     * A method that loads data for the TextFields, and ComboBoxes from the selected customer.
     * @param customer - Passes in the customer whose values will be used as the selected customer.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setCustomerController(Customer customer) throws SQLConnectionDroppedException {
        try {
            selectedCustomer = customer;
            customerIdTextField.setText(String.valueOf(selectedCustomer.getCustomerID()));
            customerNameTextField.setText(selectedCustomer.getCustomerName());
            customerAddressTextField.setText(selectedCustomer.getAddress());
            customerPostalCodeTextField.setText(selectedCustomer.getPostalCode());
            customerPhoneTextField.setText(selectedCustomer.getPhone());
            setCustomerCountryComboBox();
            divisionComboBox.setValue(customerComboBoxModel.getDivisionsComboBoxHashMap().get(selectedCustomer.getDivisionID()));
        }
        catch(SQLException | IOException e){
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * This method is overridden so that it retrieves values from this Controller instead of its parent controller.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * @see AddCustomerController#setDivisionComboBox()
     * */
    @FXML
    @Override
    public void setDivisionComboBox() throws SQLException, IOException {
        if (customerCountryId != null) {
            divisionComboBox.setDisable(false);
            customerComboBoxModel.setDivisionsComboBoxHashMap(customerCountryId);
            ArrayList<String> divisionsArrayList = new ArrayList<>();
            reverseDivisionHashMap = new HashMap<>();
            customerComboBoxModel.getDivisionsComboBoxHashMap().forEach((key, value) ->{
                divisionsArrayList.add(value);
                reverseDivisionHashMap.put(value, key);
            });
            divisionComboBox.setItems(FXCollections.observableList(divisionsArrayList));
            divisionComboBox.setPromptText(CONNECTION_MODEL.getResourceBundle().getString("divisionComboBoxPromptText"));
        }
        divisionComboBox.setOnAction(actionEvent ->
                customerDivisionId = reverseDivisionHashMap.get(divisionComboBox.getValue())
        );
    }

    /**
     * This method is overridden so that if the Modify Cusotmer Controller has just been loaded the initial value for the
     * customer country ComboBox is taken from the selected customer.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException If the database error occurs.
     * @see AddCustomerController#setCustomerCountryComboBox()
     * */
    @FXML
    @Override
    public void setCustomerCountryComboBox() throws SQLException, IOException {
        reverseCountryHashMap = new HashMap<>();
        ObservableList<String> observableCountries = countryComboBox.getItems();
        customerComboBoxModel.getCountriesComboBoxHashMap().forEach((key, value) -> {
            observableCountries.add(value);
            reverseCountryHashMap.put(value, key);
        });
        Collections.sort(observableCountries);

        if (customerCountryId == null){
            countryComboBox.setValue(getCountryFromDivisionId(selectedCustomer.getDivisionID()));
            customerCountryId = reverseCountryHashMap.get(countryComboBox.getValue());
            setDivisionComboBox();
        }

        countryComboBox.setOnAction(actionEvent -> {
            customerCountryId = reverseCountryHashMap.get(countryComboBox.getValue());
            try{
                setDivisionComboBox();
            }
            catch (SQLException | IOException e) {
                try {
                    CONNECTION_MODEL.SQLAlert();
                } catch (SQLConnectionDroppedException ex) {
                    changeSceneToLoginController(actionEvent);
                }
            }
        });
    }


    /**
     * This method is overridden so that the selected customer TextFields will never create an error flag.
     * */
    @FXML
    @Override
    public void saveCustomer() throws SQLConnectionDroppedException {
        boolean errorFlag = false;

        if (!customerNameTextField.getText().isEmpty()) {
            selectedCustomer.setCustomerName(customerNameTextField.getText());
        } else {
            addListenerToLabel(customerNameTextField, customerNameErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerNameErrorLabel"));
            errorFlag = true;
        }
        if (!customerPostalCodeTextField.getText().isEmpty() && postalCodeChecker(customerPostalCodeTextField.getText(),countryComboBox.getValue()) || customerPostalCodeTextField.getText().equals(selectedCustomer.getPostalCode())){
                selectedCustomer.setPostalCode(customerPostalCodeTextField.getText());
        } else {
            if (countryComboBox.getValue() == null) {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabel"));
            } else if (countryComboBox.getValue().equals("Canada")) {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabelCanada"));
            } else if (countryComboBox.getValue().equals("United Kingdom")) {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabelUK"));
            } else {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabel"));
            }
            errorFlag = true;
        }
        if (!customerAddressTextField.getText().isEmpty() && customerAddressCheck(customerAddressTextField) || customerAddressTextField.getText().equals(selectedCustomer.getAddress())){
            selectedCustomer.setAddress(customerAddressTextField.getText());
        }
        else {
            String customerAddressText;
            if (countryComboBox.getValue() == null){
                customerAddressText = CONNECTION_MODEL.getResourceBundle().getString("customerAddressErrorLabel");
            }
            else if (countryComboBox.getValue().equals("United Kingdom")){
                customerAddressText = CONNECTION_MODEL.getResourceBundle().getString("customerAddressErrorLabelUK");
            }
            else if (countryComboBox.getValue().equals("Canada")){
                customerAddressText = CONNECTION_MODEL.getResourceBundle().getString("customerAddressErrorLabelCanada");
            }
            else{
                customerAddressText = CONNECTION_MODEL.getResourceBundle().getString("customerAddressErrorLabel");
            }
            addListenerToLabel(customerAddressTextField, customerAddressErrorLabel, customerAddressText);
            errorFlag = true;
        }

        if (!customerPhoneTextField.getText().isEmpty() && phoneNumberCheck(customerPhoneTextField.getText(), countryComboBox.getValue()) || customerPhoneTextField.getText().equals(selectedCustomer.getPhone())){
            selectedCustomer.setPhone(customerPhoneTextField.getText());
        }
        else{
            String customerPhoneNumberErrorText;
            if (countryComboBox.getValue() == null){
                customerPhoneNumberErrorText = CONNECTION_MODEL.getResourceBundle().getString("customerPhoneErrorLabel");
            }
            else if (countryComboBox.getValue().equals("United Kingdom")){
                customerPhoneNumberErrorText = CONNECTION_MODEL.getResourceBundle().getString("customerPhoneErrorLabelUK");
            }
            else if (countryComboBox.getValue().equals("Canada")){
                customerPhoneNumberErrorText = CONNECTION_MODEL.getResourceBundle().getString("customerPhoneErrorLabelCanada");
            }
            else{
                customerPhoneNumberErrorText = CONNECTION_MODEL.getResourceBundle().getString("customerPhoneErrorLabel");
            }
            addListenerToLabel(customerPhoneTextField, customerPhoneErrorLabel, customerPhoneNumberErrorText);
            errorFlag = true;
        }
        if (errorFlag){
            return;
        }
        addCustomer(selectedCustomer);
    }

    /**
     * This method is overridden so that instead of adding a new customer to the database, the customer in the database is updated with the values from the
     * customer object created in the saveCustomer() method.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @see AddCustomerController#addCustomer(Customer)
     * */
    @Override
    public void addCustomer(Customer customer) throws SQLConnectionDroppedException {
        try{
            String sqlCommand = "UPDATE CUSTOMERS SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";

            PreparedStatement stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPostalCode());
            stmt.setString(4, customer.getPhone());
            stmt.setObject(5, ZonedDateTime.now());
            stmt.setString(6, CONNECTION_MODEL.getCurrentUsername());
            stmt.setInt(7, customer.getDivisionID());
            stmt.setInt(8, customer.getCustomerID());

            stmt.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningText"));
            alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("successText"));
            alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("customerAddedText") + " " + customer.getCustomerName() + ".");
            alert.showAndWait();
            cancelButton.fireEvent(new ActionEvent());
        }
        catch (SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * A method that returns a Country string from the division ID. This is necessary because the selected customer only includes
     * the division ID. It doesn't have any information what the country is. So the program has to go back to the database and retrieve the information itself.
     * @param divisionId The ID of the division passed in to find the associated country.
     * @return The name of the country that is associated with the division ID.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    public String getCountryFromDivisionId(int divisionId) throws SQLConnectionDroppedException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        CachedRowSet crs = null;
        String country = "";
        try{
            String query = "SELECT Country FROM COUNTRIES WHERE Country_ID = (SELECT Country_ID FROM FIRST_LEVEL_DIVISIONS WHERE Division_ID = ?)";
            preparedStatement = ConnectionModel.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(divisionId));
            rs = preparedStatement.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while (crs.next()) {
                country = crs.getString(1);
            }
        }
        catch (SQLException | IOException e){
            CONNECTION_MODEL.SQLAlert();
        }
        finally{
            closeSQLObjects(preparedStatement, rs, crs);
        }
        return country;
    }
}
