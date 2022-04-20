package com.example.appointment.controller;

import com.example.appointment.Appointment;
import com.example.appointment.ConnectionModel;
import com.example.appointment.exceptions.AppointmentDateException;
import com.example.appointment.exceptions.AppointmentEndTimeException;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

/**
 * A controller that loads the Add Appointment page allowing users to add new appointments to the database.
 * */
public class AddAppointmentController extends Controller implements AppointmentDataInterface, AddListenerInterface, ComboBoxListenerInterface, GetAppointmentsInterface, AppointmentOverlappingInterface{
    @FXML
    private TextField appointmentIdTextField;
    @FXML
    private TextField appointmentTitleTextField;
    @FXML
    private TextField appointmentDescriptionTextField;
    @FXML
    private TextField appointmentLocationTextField;
    @FXML
    private TextField appointmentTypeTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<Integer> contactIdComboBox;
    @FXML
    private ComboBox<Integer> customerIdComboBox;
    @FXML
    private ComboBox<Integer> userIdComboBox;
    @FXML
    private Spinner<Integer> startTimeHourTimeSpinner;
    @FXML
    private Spinner<Integer> startTimeMinuteTimeSpinner;
    @FXML
    private Spinner<Integer> endTimeHourTimeSpinner;
    @FXML
    private Spinner<Integer> endTimeMinuteTimeSpinner;
    @FXML
    private DatePicker appointmentStartDatePicker;
    @FXML
    private DatePicker appointmentEndDatePicker;
    @FXML
    private Label appointmentTitleErrorLabel;
    @FXML
    private Label appointmentDescriptionErrorLabel;
    @FXML
    private Label appointmentLocationErrorLabel;
    @FXML
    private Label appointmentTypeErrorLabel;
    @FXML
    private Label startTimeErrorLabel;
    @FXML
    private Label endTimeErrorLabel;
    @FXML
    private Label contactIdComboBoxErrorLabel;
    @FXML
    private Label customerIdComboBoxErrorLabel;
    @FXML
    private Label userIdComboBoxErrorLabel;

    /**
     * The ID for this appointment. The value is always last ID value in the database +1.
     * */
    protected Integer appointmentId = null;
    /**
     * The start date for this appointment. The value is selected by the user using a DatePicker.
     * */
    protected LocalDate appointmentStartDate = null;
    /**
     * The end date for this appointment. The value is selected automatically depending on the start date and time.
     * */
    protected LocalDate appointmentEndDate;
    /**
     * The business start time. Always 0800 of the day the user selects using the DatePicker.
     * */
    protected ZonedDateTime zonedBusinessStartTime = null;
    /**
     * The business end time. Always 2200 of the day the user selects using the DatePicker.
     * */
    protected ZonedDateTime zonedBusinessEndTime = null;
    /**
     * The ZoneID for the business time. Set to Eastern Standard Time.
     * */
    protected ZoneId businessZoneId = ZoneId.of("America/New_York");

    /**
     * A method that retrieves the appointment ID value from the database. The user does not set the appointment ID,
     * it is generated automatically by the program so that if they are 25 items in the database the next item will be #26.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    protected void setAppointmentId() throws SQLConnectionDroppedException {
        if (appointmentId == null) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            CachedRowSet crs = null;
            ArrayList<Integer> appointmentIdList = new ArrayList<>();
            try {
                String query = "SELECT Appointment_ID FROM APPOINTMENTS";
                stmt = ConnectionModel.getInstance().getConnection().prepareStatement(query);
                rs = stmt.executeQuery();
                crs = RowSetProvider.newFactory().createCachedRowSet();
                crs.populate(rs);
                while (crs.next()) {
                    appointmentIdList.add(crs.getInt(1));
                }
                Collections.sort(appointmentIdList);
                Collections.reverse(appointmentIdList);
                appointmentId = (appointmentIdList.get(0) + 1);

            } catch (SQLException | IOException e) {
                CONNECTION_MODEL.SQLAlert();
            } finally {
                closeSQLObjects(stmt, rs, crs);
            }
        }
        appointmentIdTextField.setText(String.valueOf(appointmentId));
    }

    /**
     * A method that sets up the TextFields for the controller. This is necessary so that each method doesn't have to be called individually.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setAppointmentController() throws SQLConnectionDroppedException {
        try {
            setAppointmentId();
            setContactIdComboBox();
            setCustomerIdComboBox();
            setUserIdComboBox();
            setAppointmentStartDatePicker();
            setHourTimeSpinner();
            setMinuteTimeSpinner();
        }
        catch(SQLException | IOException e){
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * A method that loads the contact ID ComboBox with values from the database.
     * After the ComboBox gets it values from the setComboBoxValidation method it sorts the values in numerical order.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    protected void setContactIdComboBox() throws SQLException, IOException {
        ObservableList<Integer> observableContactIds = FXCollections.observableList(getContactIdList());
        this.setIntegerComboBoxValidation(contactIdComboBox, observableContactIds);
        Collections.sort(observableContactIds);
    }

    /**
     * A method that loads the customer ID ComboBox with values from the database.
     * After the ComboBox gets it values from the setComboBoxValidation method it sorts the values in numerical order.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    protected void setCustomerIdComboBox() throws SQLException, IOException {
        ObservableList<Integer> observableCustomerIds = FXCollections.observableList(getCustomerIdList());
        this.setIntegerComboBoxValidation(customerIdComboBox, observableCustomerIds);
        Collections.sort(observableCustomerIds);
    }

    /**
     * A method that loads the user ID ComboBox with values from the database.
     * After the ComboBox gets it values from the setComboBoxValidation method it sorts the values in numerical order.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    protected void setUserIdComboBox() throws SQLException, IOException {
        ObservableList<Integer> observableUserIds = FXCollections.observableList(getUserIdList());
        this.setIntegerComboBoxValidation(userIdComboBox, observableUserIds);
        Collections.sort(observableUserIds);
    }

    /**
     * This method restricts the Start Date DatePicker so that you cannot select dates before the current day.
     * <br>
     * <em>Lambda Expression</em>
     * <br>
     * It works by calling the date picker method setDayCellFactory which can take in a lambda expression to
     * override the individual date cell's updateItem method. This allows to you customize an action for each individual cell.
     * I used it to set that all dates before the current date will be disabled. This is done by using LocalDate's compareTo method
     * that returns an integer value based on the comparison.
     * */
    @FXML
    protected void setAppointmentStartDatePicker(){
        appointmentStartDatePicker.setDayCellFactory(cell -> new DateCell(){
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date, empty);
                LocalDate currentDate = LocalDate.now();
                setDisable(empty || (date.compareTo(currentDate) < 0 ) );
            }
        });
    }

    /**
     * A method that loads the values for the hour time spinners for the start time, and end time. Both are editable, and the values
     * are checked for validity upon saving the appointment.
     * */
    @FXML
    protected void setHourTimeSpinner(){
        startTimeHourTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> startTimeHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 7);
        startTimeHourValueFactory.setWrapAround(true);
        startTimeHourTimeSpinner.setValueFactory(startTimeHourValueFactory);

        endTimeHourTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> endTimeHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 7);
        endTimeHourValueFactory.setWrapAround(true);
        endTimeHourTimeSpinner.setValueFactory(endTimeHourValueFactory);
    }

    /**
     * A method that loads the values for the minute time spinners for the start time, and end time. Both are editable, and the values
     * are checked for validity upon saving the appointment.
     * */
    @FXML
    protected void setMinuteTimeSpinner(){
        startTimeMinuteTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> startTimeMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        startTimeMinuteValueFactory.setWrapAround(true);
        startTimeMinuteTimeSpinner.setValueFactory(startTimeMinuteValueFactory);

        endTimeMinuteTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> endTimeMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        endTimeMinuteValueFactory.setWrapAround(true);
        endTimeMinuteTimeSpinner.setValueFactory(endTimeMinuteValueFactory);
    }

    /**
     * A method that sets up the business hours for the start time and end time values to be checked against.
     * Regular business hours are from 0800 to 2200 Eastern Standard Time.
     * It works by getting whatever the date the user selects and then calculating back from there. This enables it to be accurate even with time zones
     * that can start the day after eastern standard time.
     * */
    @FXML
    protected void setBusinessTimes(ZonedDateTime userZoneDateTime){
        zonedBusinessStartTime = ZonedDateTime.ofInstant(userZoneDateTime.toInstant(), businessZoneId);
        while (zonedBusinessStartTime.getHour() != 8){
            zonedBusinessStartTime = zonedBusinessStartTime.minusHours(1);
        }
        while(zonedBusinessStartTime.getMinute() != 0) {
            zonedBusinessStartTime = zonedBusinessStartTime.minusMinutes(1);
        }
        zonedBusinessEndTime = zonedBusinessStartTime.plusHours(14);
    }

    /**
     * A method that saves the appointment that was created in the controller to the database.
     * <br>
     * <em>Verifying TextFields</em>
     * <br>
     * The first thing the method does is verify data from the TextFields.
         * <br>
         * These include:
         * <ol>
         *     <li>Title</li>
         *     <li>Description</li>
         *     <li>Location</li>
         *     <li>Type</li>
         * </ol>
     * <br>
     * These are verified by making sure that their respective TextFields are no empty. If they are the error flag is set to true,
     * and a listener is added to the TextField.
     * <br>
     * Next, the date information is checked to make sure they are within range of the business time of 0800-2200 EST. The method
     * also sets the end date DatePicker based on the time entered in for the start date. If the ending hour is greater than or equal to the starting hour,
     * then the end date DatePicker will be set to the same day, if it's less than the start date hour then it will end on the next day.
     * <br>
     *     After the date has been verified the method checks the data in the ComboBoxes.
     *     <br>
     *     These include:
     *     <ol>
     *         <li>Contact ID</li>
     *         <li>Customer ID</li>
     *         <li>User ID</li>
     *     </ol>
     *     <br>
     *     If the values are not null, then the method goes on to the next step.
     * <br>
     *     After the data has been validated then an appointment object will be created that will get passed into the add appointment method where it will get added to the database.
     * @param event Passes in that the save button was pressed.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void saveAppointment(ActionEvent event) throws SQLConnectionDroppedException {
        Integer appointmentContactId = null;
        Integer appointmentCustomerId = null;
        Integer appointmentUserId = null;
        String appointmentTitle = null;
        String appointmentDescription = null;
        String appointmentLocation = null;
        String appointmentType = null;
        ZonedDateTime zonedStartDateTime = null;
        ZonedDateTime zonedEndDateTime = null;
        boolean errorFlag = false;



        if(!appointmentTitleTextField.getText().isEmpty()){
            appointmentTitle = appointmentTitleTextField.getText();
        }
        else{
            addListenerToLabel(appointmentTitleTextField, appointmentTitleErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("appointmentTitleErrorLabel"));
            errorFlag = true;
        }

        if(!appointmentDescriptionTextField.getText().isEmpty()){
            appointmentDescription = appointmentDescriptionTextField.getText();
        }
        else{
            addListenerToLabel(appointmentDescriptionTextField, appointmentDescriptionErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("appointmentDescriptionErrorLabel"));
            errorFlag = true;
        }

        if(!appointmentLocationTextField.getText().isEmpty()){
            appointmentLocation = appointmentLocationTextField.getText();
        }
        else{
            addListenerToLabel(appointmentLocationTextField, appointmentLocationErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("appointmentLocationErrorLabel"));
            errorFlag = true;
        }

        if(!(appointmentTypeTextField.getText().isEmpty())){
            appointmentType = appointmentTypeTextField.getText();
        }
        else{
            addListenerToLabel(appointmentTypeTextField, appointmentTypeErrorLabel, CONNECTION_MODEL.getResourceBundle().getString("appointmentTypeErrorLabel"));
            errorFlag = true;
        }

        try{
            if (userIdComboBox.getValue() == null){
                addListenerToComboBox(userIdComboBox, userIdComboBoxErrorLabel);
                errorFlag = true;
            }
            if (contactIdComboBox.getValue() == null){
                addListenerToComboBox(contactIdComboBox, contactIdComboBoxErrorLabel);
                errorFlag = true;
            }
            if (customerIdComboBox.getValue() == null){
                addListenerToComboBox(customerIdComboBox, customerIdComboBoxErrorLabel);
                errorFlag = true;
            }

            if(errorFlag == true){
                throw new IllegalArgumentException();
            }
            appointmentUserId = Integer.parseInt(String.valueOf(userIdComboBox.getValue()));
            appointmentContactId = Integer.parseInt(String.valueOf(contactIdComboBox.getValue()));
            appointmentCustomerId = Integer.parseInt(String.valueOf(customerIdComboBox.getValue()));
        }
        catch(IllegalArgumentException e){
            return;
        }

        try{
            appointmentStartDatePicker.setStyle(null);
            if (appointmentStartDatePicker.getValue() == null){
                addListenerToLabel(appointmentStartDatePicker, startTimeErrorLabel);
                throw new AppointmentDateException();
            }
            else{
                appointmentStartDate = appointmentStartDatePicker.getValue();
            }
            if (startTimeHourTimeSpinner.getValue() == null){
                addListenerToLabel(startTimeHourTimeSpinner, startTimeErrorLabel);
                throw new AppointmentDateException();
            }
            if (endTimeHourTimeSpinner.getValue() == null){
                addListenerToLabel(endTimeHourTimeSpinner, endTimeErrorLabel);
                throw new AppointmentDateException();
            }

            if (endTimeHourTimeSpinner.getValue() >= startTimeHourTimeSpinner.getValue()){
                appointmentEndDate = appointmentStartDate;
            }
            else{
                appointmentEndDate = appointmentStartDate.plusDays(1);
            }

            LocalTime startLocalTime = LocalTime.of(startTimeHourTimeSpinner.getValue(), startTimeMinuteTimeSpinner.getValue());
            LocalDateTime startDateLocalDateTime = LocalDateTime.of(appointmentStartDate, startLocalTime);
            OffsetDateTime offsetStartDateTime = OffsetDateTime.of(startDateLocalDateTime, ZoneId.systemDefault().getRules().getOffset(Instant.now()));
            zonedStartDateTime = ZonedDateTime.ofInstant(offsetStartDateTime.toInstant(), businessZoneId);

            LocalTime endLocalTime = LocalTime.of(endTimeHourTimeSpinner.getValue(), endTimeMinuteTimeSpinner.getValue());
            LocalDateTime endLocalDateTime = LocalDateTime.of(appointmentEndDate, endLocalTime);
            OffsetDateTime offsetEndDateTime = OffsetDateTime.of(endLocalDateTime, ZoneId.systemDefault().getRules().getOffset(Instant.now()));
            zonedEndDateTime = ZonedDateTime.ofInstant(offsetEndDateTime.toInstant(), businessZoneId);

            setBusinessTimes(zonedStartDateTime);
            try {
                if (zonedStartDateTime.isBefore(zonedBusinessStartTime) || zonedStartDateTime.isAfter(zonedBusinessEndTime)) {
                    throw new AppointmentDateException();
                }
            }
            catch(AppointmentDateException e){
                addListenerToLabel(startTimeHourTimeSpinner, startTimeErrorLabel);
                errorFlag = true;
            }

            try{
                if (zonedEndDateTime.isBefore(zonedBusinessStartTime) || zonedEndDateTime.isAfter(zonedBusinessEndTime)) {
                    throw new AppointmentDateException();
                }

                if(zonedEndDateTime.isBefore(zonedStartDateTime)){
                    throw new AppointmentEndTimeException();
                }
                appointmentEndDatePicker.setDisable(false);
                appointmentEndDatePicker.setValue(appointmentEndDate);
            }
            catch (AppointmentDateException e){
                endTimeErrorLabel.setText(CONNECTION_MODEL.getResourceBundle().getString("endTimeErrorLabelText"));
                addListenerToLabel(endTimeHourTimeSpinner, endTimeErrorLabel);
                throw new AppointmentDateException();
            }
            catch (AppointmentEndTimeException e){
                endTimeErrorLabel.setText(CONNECTION_MODEL.getResourceBundle().getString("endTimeBeforeStartTimeErrorText"));
                addListenerToLabel(endTimeHourTimeSpinner, endTimeErrorLabel);
                throw new AppointmentDateException();
            }

            List<Appointment> overlappingAppointments = isAppointmentOverlapping(appointmentCustomerId, zonedStartDateTime, zonedEndDateTime, appointmentId);
            if (!overlappingAppointments.isEmpty()){
                appointmentOverlapAlert(overlappingAppointments);
                throw new AppointmentDateException();
            }
        }
        catch (AppointmentDateException e){
            errorFlag = true;
        }

        catch (SQLException | IOException e){
            throw new SQLConnectionDroppedException();
        }
        if (errorFlag){
            return;
        }
        try{
            Appointment appointment = new Appointment(appointmentId, appointmentTitle, appointmentDescription, appointmentLocation,
                    appointmentContactId, appointmentType, zonedStartDateTime, zonedEndDateTime, appointmentCustomerId, appointmentUserId);
            addAppointment(appointment);
        }
        catch (SQLConnectionDroppedException e){
            changeSceneToLoginController(event);
        }
    }

    /**
     * A method that takes an Appointment object that is created in the saveAppointment() method and then passes the object into the database.
     * After the object has been successfully added to the database it shows an alert message to the user.
     * @param appointment A passed in appointment to be added to the database.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    public void addAppointment(Appointment appointment) throws SQLConnectionDroppedException {
        try{
            String sqlCommand = "INSERT INTO APPOINTMENTS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);

            stmt.setInt(1, appointment.getAppointmentID());
            stmt.setString(2, appointment.getTitle());
            stmt.setString(3, appointment.getDescription());
            stmt.setString(4, appointment.getLocation());
            stmt.setString(5, appointment.getType());
            stmt.setObject(6, LocalDateTime.ofInstant(appointment.getStartDate().toInstant(), businessZoneId));
            stmt.setObject(7, LocalDateTime.ofInstant(appointment.getEndDate().toInstant(), businessZoneId));
            stmt.setObject(8, ZonedDateTime.now());
            stmt.setString(9, CONNECTION_MODEL.getCurrentUsername());
            stmt.setObject(10, ZonedDateTime.now());
            stmt.setString(11, CONNECTION_MODEL.getCurrentUsername());
            stmt.setInt(12, appointment.getCustomerID());
            stmt.setInt(13, appointment.getUserID());
            stmt.setInt(14, appointment.getContactID());
            stmt.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningText"));
            alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("successText"));
            alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("appointmentAddedText") + " " + appointment.getAppointmentID() + ".");
            alert.showAndWait();
            cancelButton.fireEvent(new ActionEvent());
        }
        catch (SQLException | IOException e){
            CONNECTION_MODEL.SQLAlert();
        }
    }



    /**
     * This appointment is overridden so that if a new appointment is added it will check again to see if the appointment is within fifteen minutes of the current time.
     * @param event Takes in that a button was pressed.
     * @see Controller#changeSceneToMainController(ActionEvent)
     * */
    @FXML
    @Override
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
            ((MainController) loader.getController()).appointmentAlert(((MainController) loader.getController()).getCurrentUserAppointmentsList());
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
}
