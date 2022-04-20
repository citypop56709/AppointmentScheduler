package com.example.appointment.controller;

import com.example.appointment.*;
import com.example.appointment.exceptions.CustomerHasAppointmentsException;
import com.example.appointment.exceptions.ItemNotSelectException;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A Controller for the view customer page of the program. It displays the customer TableView and a variety of options to select different pages.
 * This also includes methods for adding, modifying and deleting customers.
 * */
public class ViewCustomersController extends MainController implements GetAppointmentsInterface, GetCustomersInterface{
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
    private TableView.TableViewSelectionModel<Customer> selectionCustomerModel;
    @FXML
    private Button logoutButton;
    private HashMap<Integer, String> customerDivisions;
    private Customer selectedCustomer = null;

    /**
     * A list of customers who have appointments. This is important because customers cannot be deleted if they have an appointment.
     * */
    protected List<Integer> customerIdsWithAppointments = null;

    /**
     * A method that sets up the customerIdsWithAppointments list so that it's updated with every customer that has an active appointment.
     * It works by getting a list of appointments for every user, if the customer ID of that appointment is not in the list of customer IDs with appointments, then it is added.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    public void setCustomersIdsWithAppointments() throws SQLConnectionDroppedException {
        try {
            customerIdsWithAppointments = new ArrayList<>();
            CONNECTION_MODEL.getConnection().setSchema("appointments");
            String sqlQuery = "SELECT * FROM APPOINTMENTS";
            PreparedStatement stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlQuery);
            ArrayList<Appointment> appointments = new ArrayList<>(getAppointmentList(stmt));
            for (Appointment appointment : appointments) {
                if (!customerIdsWithAppointments.contains(appointment.getCustomerID())) {
                    customerIdsWithAppointments.add(appointment.getCustomerID());
                }
            }
        }
        catch(SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * A method that sets up the customer division name HashMap so that it can be accessed for by the division ComboBox.
     * This works by connecting to the database, and retrieving the division ID as they key, and the Division as the value, for each customer.
     * @return A HashMap of the customer division ID as they key and the division as the value.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    protected HashMap<Integer, String> setCustomerDivisionNameHashMap() throws SQLConnectionDroppedException {
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
        }
        finally{
            closeSQLObjects(stmt,rs, crs);
        }
        return customerDivisions;
    }


    /**
     * A method that sets up the TableView object by retrieving customer objects from the database, adding them
     * to an observable list and then displaying that list in the table.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException If the database error occurs.
     * */
    @FXML
    public void setCustomerTableView() throws IOException, SQLException {
        CachedRowSet crs = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM CUSTOMERS";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
            SortedList<Customer> sortedAppointmentList = new SortedList<>(getCustomerList(stmt));
            sortedAppointmentList.comparatorProperty().bind(customerTableView.comparatorProperty());
            customerDivisions = setCustomerDivisionNameHashMap();
            customerTableView.setItems(sortedAppointmentList);
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
            selectionCustomerModel = customerTableView.getSelectionModel();
            selectionCustomerModel.setSelectionMode(SelectionMode.SINGLE);
        } catch (SQLException e) {
            CONNECTION_MODEL.SQLAlert();
            logoutButton.fireEvent(new ActionEvent());
        }
        finally{
            closeSQLObjects(stmt,rs, crs);
        }
    }

    /**
     * A method that changes the scene to the add customers page. It loads the setCustomerCountryComboBox method so that the countries,
     * and divisions are selectable from the controller.
     * @param event Takes in that the Add Customer button was pressed.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException If the database error occurs.
     * */
    @FXML
    public void changeAddCustomerScene(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("add-customer.fxml"), CONNECTION_MODEL.getResourceBundle());
        Parent root = loader.load();
        ((AddCustomerController)loader.getController()).setCustomerComboBoxModel();
        ((AddCustomerController)loader.getController()).setCustomerController(event);
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("addCustomerHeaderTextLabel"));
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }

    /**
     * Changes the scene to the modify customer scene. If no customer is selected from the table an error message is generated.
     * @param event Passes in that the modify customer button was pressed.
     * @throws IOException If the fxml file cannot be found, or if a different I/O error occurs.
     * */
    @FXML
    public void changeModifyCustomerScene(ActionEvent event) throws IOException {
        try{
            selectedCustomer = getSelectedCustomerFromTable();
            if (selectedCustomer == null){
                throw new ItemNotSelectException(CONNECTION_MODEL.getResourceBundle().getString("itemNotSelectedContentText"));
            }
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("modify-customer.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((ModifyCustomerController)loader.getController()).setCustomerComboBoxModel();
            ((ModifyCustomerController) loader.getController()).setCustomerController(selectedCustomer);
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (ItemNotSelectException e){
            e.Alert();
        }
        catch (SQLException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException ex){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * A method that allows a selected customer to be deleted from the database. After the customer is deleted the setCustomerTableView method
     * is called again to refresh the displayed customers. If no customer is selected it will generate an error message from the
     * getSelectedCustomerFromTable() method. If the customer has an appointment then the method throws an exception that is caught and
     * generates an error message.
     * @param event - Passes in that the delete customer button was pressed.
     * */
    public void deleteCustomer(ActionEvent event){
        PreparedStatement stmt = null;
        try{
            selectedCustomer = getSelectedCustomerFromTable();
            if (customerIdsWithAppointments.contains(selectedCustomer.getCustomerID())){
                throw new CustomerHasAppointmentsException();
            }
            else{
                String query = "DELETE FROM CUSTOMERS WHERE Customer_ID = ?";
                stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
                stmt.setString(1, String.valueOf(selectedCustomer.getCustomerID()));
                stmt.executeUpdate();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningText"));
                alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("deleteText"));
                alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("customerIdDeletedText") + " " + selectedCustomer.getCustomerName() + ".");
                selectedCustomer = null;
                alert.showAndWait();
                setCustomerTableView();
            }
        }
        catch (SQLException | IOException e){
            try{
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException ex){
                changeSceneToLoginController(event);
            }
        }
        catch (CustomerHasAppointmentsException e){
            e.Alert();
        }
        finally{
            try {
                CONNECTION_MODEL.closeSQLObjects(stmt);
            }
            catch (SQLConnectionDroppedException e){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * Gets the selected customer object from the TableView so that it can be passed into a method to be modified or deleted.
     * @return The selected customer from the TableView.
     * */
    @FXML
    public Customer getSelectedCustomerFromTable(){
        return selectionCustomerModel.getSelectedItem();
    }

    /**
     * A method that changes the scene to the main controller page to display appointments by week when the view all appointments radio button is pressed.
     * This method is different from the main controller's changeViewWeek method because the method has to switch the controller and then call the changeViewByWeek method.
     * @param event Takes in that the View by Week radio button was pressed.
     * */
    @FXML
    public void changeSceneToMainControllerViewByWeek(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appointment/main-menu.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((MainController)loader.getController()).changeViewByWeek(event);
            ((MainController)loader.getController()).setZoneIdLabel();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("mainMenuHeaderTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (IOException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException ex){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * A method that changes the scene to the main controller page to display appointments by month when the view all appointments radio button is pressed.
     * This method is different from the main controller's changeViewByMonth method because the method has to switch the controller and then call the changeViewMonth method.
     * @param event Takes in that the View by Month radio button was pressed.
     * */
    @FXML
    public void changeSceneToMainControllerViewByMonth(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appointment/main-menu.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((MainController)loader.getController()).changeViewByMonth(event);
            ((MainController)loader.getController()).setZoneIdLabel();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("mainMenuHeaderTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (IOException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException ex){
                changeSceneToLoginController(event);
            }
        }
    }
}
