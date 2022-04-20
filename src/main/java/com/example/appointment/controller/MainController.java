package com.example.appointment.controller;

import com.example.appointment.*;
import com.example.appointment.exceptions.ItemNotSelectException;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *  A Controller for the main page of the program. It displays the appointment TableView and a variety of options to select different pages.
 * This also includes methods for adding, modifying, and deleting appointments.
 * */
public class MainController extends Controller implements GetAppointmentsInterface, AppointmentAlertsInterface{
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentLocationColumn;
    @FXML
    private TableColumn<Appointment, String> contactInfoColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentStartTimeColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentEndTimeColumn;
    @FXML
    private TableColumn<Appointment, String> startDateColumn;
    @FXML
    private TableColumn<Appointment, String> endDateColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> userIdColumn;
    @FXML
    private TableView.TableViewSelectionModel<Appointment> selectionAppointmentModel;
    @FXML
    private RadioButton viewByWeekRadioButton;
    @FXML
    private RadioButton viewByMonthRadioButton;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private Label yourTimeZoneLabel;

    private ArrayList<Appointment> currentUserAppointments = new ArrayList<>();
    private Appointment selectedAppointment;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private PreparedStatement stmt = null;

    /**
     * Changes the Time Zone label to the user's current ZoneID.
     * */
    public void setZoneIdLabel(){
        String timeZoneText = (CONNECTION_MODEL.getResourceBundle().getString("yourTimeZoneLabel") + " " + ZoneId.systemDefault());
        yourTimeZoneLabel.setText(timeZoneText);
    }

    /**
     * A method that returns a list of appointments that matches the appointments in the TableView.
     * @return An array list of appointments for the current user.
     * */
    public ArrayList<Appointment> getCurrentUserAppointmentsList(){
        return currentUserAppointments;
    }

    /**
     * A method that sets up the TableView object by retrieving appointment objects from the database, placing them into a user object's
     * appointment list, and then displaying that list in the table.
     * <br>
     * They are four columns that have lambda expressions in this method
     *     <ol>
     *         <li>appointmentStartTimeColumn</li>
     *         <li>appointmentEndTimeColumn</li>
     *         <li>startDateColumn</li>
     *         <li>endDateColumn</li>
     *     </ol>
     * <br>
     * The two time columns work by formatting the appointment date objects into the time format HH:mm for each cell in the column.
     * The two date columns work the same way and format each appointment date object to the format of MM-dd-yyyy.
     *  These lambda expressions are an easy way to make the time and date information readable and still convertable with
     *  the actual appointment object in the database.
     * @param stmt A PreparedStatement that is passed in based on what radio button is pressed. This enables the method to only pull
     *             select appointments from the database. It also prevents the entire code from being copied three times.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setTableView(PreparedStatement stmt) throws SQLConnectionDroppedException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList(getAppointmentList(stmt));
        SortedList<Appointment> sortedAppointmentList = new SortedList<>(appointments);
        sortedAppointmentList.comparatorProperty().bind(appointmentTableView.comparatorProperty());
        appointmentTableView.setItems(sortedAppointmentList);
        appointmentIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("appointmentID")
        );
        appointmentTitleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title")
        );
        appointmentDescriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );
        appointmentLocationColumn.setCellValueFactory(
                new PropertyValueFactory<>("location")
        );
        contactInfoColumn.setCellValueFactory(
                new PropertyValueFactory<>("contactID")
        );
        appointmentTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );
        appointmentStartTimeColumn.setCellValueFactory (appointment -> new SimpleStringProperty(ZonedDateTime.ofInstant(appointment.getValue().getStartDate().toInstant(), ZoneId.systemDefault()).format(dateTimeFormatter)));

        appointmentEndTimeColumn.setCellValueFactory (appointment -> new SimpleStringProperty(ZonedDateTime.ofInstant(appointment.getValue().getEndDate().toInstant(), ZoneId.systemDefault()).format(dateTimeFormatter)));

        startDateColumn.setCellValueFactory(appointment -> new SimpleStringProperty(ZonedDateTime.ofInstant(appointment.getValue().getStartDate().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM-dd-yyyy", CONNECTION_MODEL.getCurrentLocale()))));

        endDateColumn.setCellValueFactory( appointment -> new SimpleStringProperty(ZonedDateTime.ofInstant(appointment.getValue().getEndDate().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM-dd-yyyy", CONNECTION_MODEL.getCurrentLocale()))));

        customerIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("customerID")
        );
        userIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("userID")
        );
        appointmentTableView.setPlaceholder(new Label(CONNECTION_MODEL.getResourceBundle().getString("tableSetAppointmentsPlaceHolderText")));
        selectionAppointmentModel = appointmentTableView.getSelectionModel();
        selectionAppointmentModel.setSelectionMode(SelectionMode.SINGLE);
        currentUserAppointments = new ArrayList<>(sortedAppointmentList);
    }


    /**
     * Gets the selected appointment object from the TableView so that it can be passed into a method to be modified or deleted.
     * @return The selected appointment from the TableView.
     * */
    @FXML
    public Appointment getSelectedAppointmentFromTable(){
        return selectionAppointmentModel.getSelectedItem();
    }

    /**
     * A method that deletes an appointment from the database. It works by getting a selectedAppointment object,
     * and then passing the ID of the appointment into the database where it's deleted. After it's been deleted
     * the table is redrawn so that it shows that the appointment was deleted.
     * @param event Passes in that the delete appointment button was pressed.
     * */
    @FXML
    public void deleteAppointment(ActionEvent event){
        PreparedStatement stmt = null;
        try{
            selectedAppointment = getSelectedAppointmentFromTable();
            if (selectedAppointment == null){
                throw new ItemNotSelectException(CONNECTION_MODEL.getResourceBundle().getString("appointmentNotSelectedContentText"));
            }
            String query = "DELETE FROM APPOINTMENTS WHERE Appointment_ID = ?";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
            stmt.setString(1, String.valueOf(selectedAppointment.getAppointmentID()));
            stmt.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningText"));
            alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("deleteAppointmentHeaderText"));
            alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("deletedAppointmentIDText") + selectedAppointment.getAppointmentID()
            + " " + CONNECTION_MODEL.getResourceBundle().getString("deletedAppointmentTypeText") + selectedAppointment.getType());
            selectedAppointment = null;
            alert.showAndWait();
            changeViewAll(event);
        }
        catch (SQLException | IOException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
        catch (ItemNotSelectException e){
            e.Alert();
        }
        finally{
            try {
                CONNECTION_MODEL.closeSQLObjects(stmt);
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * A method that changes the scene to the add appointments page when the add appointment button is pressed.
     * @param event Takes in that the Add Appointment button was pressed.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    public void changeAddAppointmentScene(ActionEvent event) throws IOException{
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("add-appointment.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((AddAppointmentController) loader.getController()).setAppointmentController();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("addAppointmentHeaderTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (SQLConnectionDroppedException e){
            changeSceneToLoginController(event);
        }
    }

    /**
     * A method that changes the scene to the modify appointments page when the modify appointment button is pressed.
     * It will generate an alert message if no appointment has been selected already to be modified.
     * @param event Takes in that the Modify Appointment button was pressed.
     * */
    @FXML
    public void changeModifyAppointmentScene(ActionEvent event){
        try {
            selectedAppointment = getSelectedAppointmentFromTable();
            if (selectedAppointment == null) {
                throw new ItemNotSelectException(CONNECTION_MODEL.getResourceBundle().getString("itemNotSelectedContentText"));
            }
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("modify-appointment.fxml"), CONNECTION_MODEL.getResourceBundle());
            Parent root = loader.load();
            ((ModifyAppointmentController) loader.getController()).setSelectedAppointment(selectedAppointment);
            ((ModifyAppointmentController) loader.getController()).setAppointmentController();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle(CONNECTION_MODEL.getResourceBundle().getString("modifyAppointmentHeaderTextLabel"));
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        catch (ItemNotSelectException e){
            e.Alert();
        }
        catch (IOException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
        catch (SQLConnectionDroppedException e){
            changeSceneToLoginController(event);
        }
    }

    /**
     * A method that creates a query when the appropriate radio button is pressed and then
     * passes it into the setTableView method to load the TableView.
     * This enables the table to display all the appointments, without having to rewrite the entire method multiple times.
     * @param event Passes in that the View All radio button was pressed.
     * @throws IOException If an input/output error occurs.
     * */
    public void changeViewAll(ActionEvent event) throws IOException {
        try {
            String sqlQuery = "SELECT * FROM APPOINTMENTS WHERE User_ID = ?";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlQuery);
            stmt.setString(1, String.valueOf(CONNECTION_MODEL.getUserId()));
            setTableView(stmt);
        }
        catch(SQLException | IOException e) {
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * A method that creates a prepared statement when the appropriate radio button is pressed and then
     * passes it into the setTableView method to load the TableView.
     * This enables the table to display all the appointments within the current month, without having to rewrite the entire method multiple times.
     * @param event Passes in that the View By Month radio button was pressed.
     * @throws IOException If an input/output error occurs.
     * */
    public void changeViewByMonth(ActionEvent event) throws IOException {
        try {
            viewByMonthRadioButton.setSelected(true);
            String sqlQuery = "SELECT * FROM APPOINTMENTS WHERE User_ID = ? AND month(Start) = ? AND year(Start) = ?";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlQuery);
            stmt.setInt(1, CONNECTION_MODEL.getUserId());
            stmt.setInt(2, CONNECTION_MODEL.getCurrentTime().getMonth().getValue());
            stmt.setInt(3, CONNECTION_MODEL.getCurrentTime().getYear());
            setTableView(stmt);
        }
        catch(SQLException | IOException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * A method that creates a prepared statement when the appropriate radio button is pressed and then
     * passes it into the setTableView method to load the TableView.
     * This enables the table to display all the appointments that are due that day, without having to rewrite the entire method multiple times.
     * @param event Passes in that the View By Day radio button was pressed.
     * */
    @FXML
    public void changeViewByDay(ActionEvent event){
        try {
            String sqlQuery = "SELECT * FROM APPOINTMENTS WHERE User_ID = ? AND date(Start) = ?";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlQuery);
            stmt.setInt(1, CONNECTION_MODEL.getUserId());
            stmt.setDate(2, Date.valueOf(appointmentDatePicker.getValue()));
            setTableView(stmt);
        }
        catch(SQLException | IOException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * A method that creates a prepared statement when the appropriate radio button is pressed and then
     * passes it into the setTableView method to load the TableView.
     * This enables the table to display all the appointments within the current week, without having to rewrite the entire method multiple times.
     * @param event Passes in that the View By Week radio button was pressed.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    public void changeViewByWeek(ActionEvent event) throws IOException{
        try {
            viewByWeekRadioButton.setSelected(true);
            CONNECTION_MODEL.getConnection().setSchema("appointments");
            String sqlQuery = "SELECT * FROM APPOINTMENTS WHERE User_ID = ? AND YEARWEEK(Start)=YEARWEEK(NOW());";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlQuery);
            stmt.setInt(1, CONNECTION_MODEL.getUserId());
            setTableView(stmt);
        }
        catch (SQLException | IOException e){
            try {
                CONNECTION_MODEL.SQLAlert();
            }
            catch (SQLConnectionDroppedException f){
                changeSceneToLoginController(event);
            }
        }
    }

    /**
     * A method that changes the scene to the appointment reports page when the Reports button is pressed.
     * @param event Takes in that the Reports button was pressed.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException If the database error occurs.
     * */
    @FXML
    public void changeReportsScene(ActionEvent event) throws IOException, SQLException {
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
}