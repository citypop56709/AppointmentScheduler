package com.example.appointment.controller;

import com.example.appointment.*;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A controller that loads the Total Customer Type Reports scene. This scene enables the user to select a type and see the total number of customers for that type.
 * */
public class TotalCustomersTypeReportController extends ReportsController implements GetCustomersInterface, ComboBoxListenerInterface{
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
    private ComboBox<String> appointmentTypeComboBox;
    @FXML
    private Button logoutButton;
    @FXML
    private Label totalCustomersLabel;
    private PreparedStatement stmt;

    /**
     * A HashMap that saves the customer divisions as an integer key and the actual name of the division as a value. This way the TableView can display the customer division name
     * instead of just an ID.
     * */
    protected HashMap<Integer, String> customerDivisions;

    /**
     * A method that loads the type combo box with all the available types of appointments from the database.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setTypeComboBox() throws SQLConnectionDroppedException {
        ObservableList<String> observableTypes = FXCollections.observableList(getAppointmentTypeList());
        Collections.sort(observableTypes);
        setStringComboBoxValidation(appointmentTypeComboBox, observableTypes);
    }

    /**
     * A method that retrieves a list of appointment types by accessing the database. It gets the
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @return A list of various appointment types.
     * */
    public List<String> getAppointmentTypeList() throws SQLConnectionDroppedException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        CachedRowSet crs = null;
        List<String> types = new ArrayList<>();
        try {
            String sqlCommand = "SELECT Type FROM APPOINTMENTS";
            preparedStatement = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);
            rs = preparedStatement.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while(crs.next()){
                if(!types.contains(crs.getString(1))){
                    types.add(crs.getString(1));
                }
            }
        } catch (SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
        }
        finally{
            closeSQLObjects(preparedStatement, rs, crs);
        }
        return types;
    }

    /**
     * A method that retrieves a HashMap of customer divisions so that instead of division IDs the table view can display the actual name of the division.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @return  A HashMap of customer division IDs and division names.
     * */
    protected HashMap<Integer, String> setCustomerDivisionNameList() throws SQLConnectionDroppedException {
        CachedRowSet crs = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        HashMap<Integer, String> customerDivisions = new HashMap<>();
        try{
            String query = "SELECT Division_ID, Division FROM FIRST_LEVEL_DIVISIONS WHERE Division_ID IN (SELECT Division_ID FROM CUSTOMERS)";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
            rs = stmt.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while (crs.next()){
                customerDivisions.put(crs.getInt(1), crs.getString(2));
            }
        }
        catch (SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
            logoutButton.fireEvent(new ActionEvent());
        }
        finally{
            closeSQLObjects(stmt,rs, crs);
        }
        return customerDivisions;
    }

    /**
     * A method that retrieves all customers that have appointments with the given type, then it adds the customers to an observable list, that are displayed in the TableView.
     * @param event Passes in the data for the button that opened the controller. This data is used to log out if there's a SQL error.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setCustomerTableView(ActionEvent event) throws SQLConnectionDroppedException {
        try {
            String type = "";
            if ((event.getSource() instanceof ComboBox)){
                type = (String) ((ComboBox<?>) event.getSource()).getValue();
            }
            String query = "SELECT * FROM CUSTOMERS WHERE Customer_ID IN (SELECT APPOINTMENTS.Customer_ID FROM APPOINTMENTS WHERE Type = ?)";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
            stmt.setString(1, type);
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
            CONNECTION_MODEL.SQLAlert();
        }
        finally{
            closeSQLObjects(stmt);
        }
    }

    /**
     * A method that sets up the total customers label so that it displays the total number of customers in the TableView.
     * @param total - Passes in an integer representing the total number of customers.
     * */
    @FXML
    public void setTotalCustomersLabel(int total){
        totalCustomersLabel.setText(CONNECTION_MODEL.getResourceBundle().getString("totalCustomersLabel") + total);
    }
}
