package com.example.appointment.controller;

import com.example.appointment.*;
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
import java.time.*;
import java.util.*;

/**
 * A controller that loads the Add Customer page allowing users to add new customers to the database.
 * */
public class AddCustomerController extends Controller implements AddListenerInterface, AddressCheckInterface, PostalCodeCheckInterface, PhoneNumberCheckInterface{
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

    private Integer customerId = null;

    /**
     *
     * */
    protected Integer customerCountryId = null;
    /**
     *
     * */
    protected Integer customerDivisionId = null;
    /**
     * A HashMap that takes in a String of the Country name as a key, and the Country ID as a value.
     * This enables the Country ComboBox to retrieve the country names based on their IDs and allows
     * the division ComboBox to load its divisions based on the Country name.
     * */
    protected HashMap<String, Integer> reverseCountryHashMap;
    /**
     * A HashMap that takes in a String of the division name as a key, and the division ID as a value.
     * This enables the Division ComboBox to retrieve the division names based on their IDs.
     * */
    protected HashMap<String, Integer> reverseDivisionHashMap;


    /**
     * A Singleton instance that manages the customer data from the database.
     * */
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
     * A method that calls the methods that set up the TextFields and ComboBoxes for the controller. This is necessary so that each method doesn't have to be called individually.
     * @param event Passes in that the button to open the controller was pressed.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setCustomerController(ActionEvent event) throws SQLConnectionDroppedException {
        try {
            setCustomerId(event);
            setCustomerCountryComboBox();
        }
        catch(SQLException | IOException e){
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * A method that retrieves the customer ID value from the database. The user does not set the customer ID,
     * it is generated automatically by the program so that if they are 25 items in the database the next item will be #26.
     * @param event Passes in the button that was pressed to open the controller.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    protected void setCustomerId(ActionEvent event) throws SQLConnectionDroppedException {
        if (customerId == null){
            PreparedStatement stmt = null;
            ResultSet rs = null;
            CachedRowSet crs = null;
            ArrayList<Integer> customerIdList = new ArrayList<>();
            try{
                String query = "SELECT Customer_ID FROM CUSTOMERS";
                stmt = ConnectionModel.getInstance().getConnection().prepareStatement(query);
                rs = stmt.executeQuery();
                crs = RowSetProvider.newFactory().createCachedRowSet();
                crs.populate(rs);
                while (crs.next()){
                    customerIdList.add(crs.getInt("Customer_ID"));
                }
                Collections.sort(customerIdList);
                Collections.reverse(customerIdList);
                customerIdTextField.setText(String.valueOf(customerIdList.get(0) + 1));
                customerId = Integer.valueOf(customerIdTextField.getText());
            }
            catch (SQLException | IOException e){
                CONNECTION_MODEL.SQLAlert();
            }
            finally {
                closeSQLObjects(stmt, rs, crs);
            }
        }
    }

    /**
     * A method that loads the customer country ComboBox with values from the database.
     * After the ComboBox gets it values from the setComboBoxValidation method it sorts the values in numerical order.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    public void setCustomerCountryComboBox() throws SQLException, IOException{
        reverseCountryHashMap = new HashMap<>();
        ObservableList<String> observableCountries = countryComboBox.getItems();
        customerComboBoxModel.getCountriesComboBoxHashMap().forEach((key, value) -> {
            observableCountries.add(value);
            reverseCountryHashMap.put(value, key);
        });
        Collections.sort(observableCountries);
        countryComboBox.setOnAction(actionEvent -> {
            customerCountryId = reverseCountryHashMap.get(countryComboBox.getValue());
            try {
                setDivisionComboBox();
            } catch (SQLException | IOException e) {
                try {
                    CONNECTION_MODEL.SQLAlert();
                } catch (SQLConnectionDroppedException ex) {
                    changeSceneToLoginController(actionEvent);
                }
            }
        });
    }

    /**
     * A method that loads the customer country ComboBox with values from the database.
     * After the ComboBox gets it values from the setComboBoxValidation method it sorts the values in numerical order.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
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
     * A method that validates the customer's address.
     * @param textField The Customer Address TextField whose address will be verified.
     * @return true or false depending on the addresses validity.
     * @see AddressCheckInterface#addressCheck(String, int)
     * */
    protected boolean customerAddressCheck(TextField textField){
        if (!textField.getText().isEmpty()){
            if (countryComboBox.getValue().equals("U.S")){
                return addressCheck(textField.getText(), 2);
            }
            else if (countryComboBox.getValue().equals("Canada")){
                return addressCheck(textField.getText(), 2);
            }
            else{
                return addressCheck(textField.getText(), 3);
            }
        }
        return false;
    }

    /**
     * A method that saves the customer that was created in the controller to the database.
     * <br>
     * <em>Verifying TextFields</em>
     * <br>
     * The first thing the method does is verify data from the TextFields.
         * <br>
         * These include:
         * <ol>
         *     <li>Name</li>
         *     <li>Address</li>
         *     <li>Postal Code</li>
         *     <li>Phone</li>
         * </ol>
         * <br>
         * These are verified by making sure that their respective TextFields are no empty. If they are the error flag is set to true,
         * and a listener is added to the TextField.
     * <br>
     *     After the TextFields are verified the method checks the data in the ComboBoxes.
     *     <br>
     *     These include:
     *     <ol>
     *         <li>Country ComboBox</li>
     *         <li>Division ComboBox</li>
     *     </ol>
     *     <br>
     *     The divisions cannot be selected until the country is because the available divisions are dependent on what country was selected.
     * <br>
     *     Next, the following data is added to each customer:
     *     <br>
     *     <ol>
     *         <li>Created by username</li>
     *         <li>Last updated by username</li>
     *         <li>Created by date</li>
     *         <li>Last updated by date</li>
     *     </ol>
     *     Because this is a new customer both username values will be the same, and both date values will be the present date and time with time zone information.
     * <br>
     * Finally, when all the data has been validated then a customer object will be created that will get passed into the add customer method where it will get added to the database.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void saveCustomer() throws SQLConnectionDroppedException {
        String customerName = null;
        String customerAddress = null;
        String customerPostalCode = null;
        String customerPhone = null;
        boolean errorFlag = false;

        if (!customerNameTextField.getText().isEmpty()) {
            customerName = customerNameTextField.getText();
        } else {
            addListenerToLabel(customerNameTextField, customerNameErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerNameErrorLabel"));
            errorFlag = true;
        }
        if (!customerPostalCodeTextField.getText().isEmpty() && postalCodeChecker(customerPostalCodeTextField.getText(),countryComboBox.getValue())) {
            customerPostalCode = customerPostalCodeTextField.getText();
        } else {
            if (countryComboBox.getValue() == null) {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabel"));
            } else if (countryComboBox.getValue().equals("Canada")) {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabelCanada"));
            } else if (countryComboBox.getValue().equals("UK")) {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabelUK"));
            } else {
                addListenerToLabel(customerPostalCodeTextField, customerPostalCodeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("customerPostalCodeErrorLabel"));
            }
            errorFlag = true;
        }

        if (!customerAddressTextField.getText().isEmpty() && customerAddressCheck(customerAddressTextField)){
            customerAddress = customerAddressTextField.getText();
        }
        else {
            String customerAddressText;
            if (countryComboBox.getValue() == null){
                customerAddressText = CONNECTION_MODEL.getResourceBundle().getString("customerAddressErrorLabel");
            }
            else if (countryComboBox.getValue().equals("UK")){
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

        if (!customerPhoneTextField.getText().isEmpty() && phoneNumberCheck(customerPhoneTextField.getText(), countryComboBox.getValue())){
            customerPhone = customerPhoneTextField.getText();
        }
        else{
            String customerPhoneNumberErrorText;
            if (countryComboBox.getValue() == null){
                customerPhoneNumberErrorText = CONNECTION_MODEL.getResourceBundle().getString("customerPhoneErrorLabel");
            }
            else if (countryComboBox.getValue().equals("UK")){
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

        ZonedDateTime currentDateTime = Instant.now().atZone(ZoneId.systemDefault());
        String createdBy = CONNECTION_MODEL.getCurrentUsername();

        Customer customer = new Customer(customerId, customerName, customerAddress, customerPostalCode, customerPhone, currentDateTime, createdBy, currentDateTime, createdBy, customerDivisionId);
        addCustomer(customer);
    }

    /**
     * A method that takes an Customer object that is created in the saveCustomer() method and then passes the object into the database.
     * After the object has been successfully added to the database it shows an alert message to the user.
     * @param customer A passed in customer to be added to the database.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    public void addCustomer(Customer customer) throws SQLConnectionDroppedException {
        try{
            CONNECTION_MODEL.getConnection().setSchema("customers");
            String sqlCommand = "INSERT INTO CUSTOMERS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);

            stmt.setInt(1, customer.getCustomerID());
            stmt.setString(2, customer.getCustomerName());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getPostalCode());
            stmt.setString(5, customer.getPhone());
            stmt.setObject(6, customer.getCreateDate());
            stmt.setString(7, customer.getCreatedBy());
            stmt.setObject(8, customer.getLastUpdate());
            stmt.setString(9, customer.getLastUpdatedBy());
            stmt.setInt(10, customer.getDivisionID());

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
}
