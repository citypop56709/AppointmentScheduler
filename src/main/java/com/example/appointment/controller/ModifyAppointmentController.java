package com.example.appointment.controller;

import com.example.appointment.Appointment;
import com.example.appointment.exceptions.AppointmentDateException;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.*;
import java.util.Collections;
import java.util.List;

/**
 * A controller that loads the Modify Appointment page allowing users to modify selected appointments from the database.
 * */
public class ModifyAppointmentController extends AddAppointmentController{
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

    private Appointment selectedAppointment;

    /**
     * A method that sets up the values for the TextFields using data from the selected appointment.
     * @param appointment - Passes in an appointment to be set as the selected appointment that the data will be loaded from.
     * */
    public void setSelectedAppointment(Appointment appointment){
        selectedAppointment = appointment;
        appointmentId = selectedAppointment.getAppointmentID();
        appointmentIdTextField.setText(String.valueOf(appointmentId));
        appointmentTitleTextField.setText(selectedAppointment.getTitle());
        appointmentDescriptionTextField.setText(selectedAppointment.getDescription());
        appointmentLocationTextField.setText(selectedAppointment.getLocation());
        appointmentTypeTextField.setText(selectedAppointment.getType());
    }

    /**
     * This method is overridden because it changes the value of the ComboBox to match that of the selected appointment.
     * @see AddAppointmentController#setContactIdComboBox()
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    @Override
    public void setContactIdComboBox() throws SQLException, IOException {
        ObservableList<Integer> observableContactIds = FXCollections.observableList(getContactIdList());
        Collections.sort(observableContactIds);
        this.setIntegerComboBoxValidation(contactIdComboBox, observableContactIds);
        contactIdComboBox.setValue(selectedAppointment.getContactID());
    }

    /**
     * This method is overridden because it changes the value of the ComboBox to match that of the selected appointment.
     * @see AddAppointmentController#setCustomerIdComboBox()
     * @throws SQLException If a SQL database error occurs.
     * */
    @FXML
    @Override
    public void setCustomerIdComboBox() throws SQLException{
        ObservableList<Integer> observableCustomerIds = FXCollections.observableList(getCustomerIdList());
        Collections.sort(observableCustomerIds);
        this.setIntegerComboBoxValidation(customerIdComboBox, observableCustomerIds);
        customerIdComboBox.setValue(selectedAppointment.getCustomerID());
    }

    /**
     * This method is overridden because it changes the value of the ComboBox to match that of the selected appointment.
     * @see AddAppointmentController#setUserIdComboBox()
     * @throws SQLException If a SQL database error occurs.
     * */
    @FXML
    @Override
    public void setUserIdComboBox() throws SQLException{
        ObservableList<Integer> observableUserIds = FXCollections.observableList(getUserIdList());
        Collections.sort(observableUserIds);
        this.setIntegerComboBoxValidation(userIdComboBox, observableUserIds);
        userIdComboBox.setValue(selectedAppointment.getUserID());
    }

    /**
     * This method is overridden so that after the DatePickers are set up the initial values are set from the selected appointment.
     * @see AddAppointmentController#setAppointmentStartDatePicker()
     * */
    @FXML
    @Override
    public void setAppointmentStartDatePicker(){
        appointmentStartDatePicker.setDayCellFactory(cell -> new DateCell(){
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date, empty);
                LocalDate currentDate = LocalDate.now();
                setDisable(empty || (date.compareTo(currentDate) < 0 ) );
            }
        });
        appointmentStartDatePicker.setValue(LocalDate.ofInstant(selectedAppointment.getStartDate().toInstant(), ZoneId.systemDefault()));
        appointmentEndDatePicker.setValue(LocalDate.ofInstant(selectedAppointment.getEndDate().toInstant(), ZoneId.systemDefault()));
    }

    /**
     * This method is overridden so that after the spinners are set up the initial values for the spinners can be taken from the selected appointment.
     * @see AddAppointmentController#setHourTimeSpinner()
     * */
    @FXML
    @Override
    public void setHourTimeSpinner(){
        startTimeHourTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> startTimeHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 7);
        startTimeHourValueFactory.setWrapAround(true);
        startTimeHourTimeSpinner.setValueFactory(startTimeHourValueFactory);
        startTimeHourValueFactory.setValue(LocalDateTime.ofInstant(selectedAppointment.getStartDate().toInstant(), ZoneId.systemDefault()).getHour());

        endTimeHourTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> endTimeHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 7);
        endTimeHourValueFactory.setWrapAround(true);
        endTimeHourTimeSpinner.setValueFactory(endTimeHourValueFactory);
        endTimeHourValueFactory.setValue(LocalDateTime.ofInstant(selectedAppointment.getEndDate().toInstant(), ZoneId.systemDefault()).getHour());
    }

    /**
     * The method is overridden so that after the spinners are set up the initial values can be taken from the selected appointment.
     * @see AddAppointmentController#setMinuteTimeSpinner()
     * */
    @FXML
    @Override
    public void setMinuteTimeSpinner(){
        startTimeMinuteTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> startTimeMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        startTimeMinuteValueFactory.setWrapAround(true);
        startTimeMinuteTimeSpinner.setValueFactory(startTimeMinuteValueFactory);
        startTimeMinuteValueFactory.setValue(LocalDateTime.ofInstant(selectedAppointment.getStartDate().toInstant(), ZoneId.systemDefault()).getMinute());

        endTimeMinuteTimeSpinner.setEditable(true);
        SpinnerValueFactory<Integer> endTimeMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        endTimeMinuteValueFactory.setWrapAround(true);
        endTimeMinuteTimeSpinner.setValueFactory(endTimeMinuteValueFactory);
        endTimeMinuteValueFactory.setValue(LocalDateTime.ofInstant(selectedAppointment.getEndDate().toInstant(), ZoneId.systemDefault()).getMinute());
    }

    /**
     * This method is overridden so that it accesses the elements from this controller instead of the parent controller.
     * @see AddAppointmentController#saveAppointment(ActionEvent)
     * */
    @FXML
    @Override
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
            appointmentUserId = Integer.parseInt(String.valueOf(userIdComboBox.getValue()));
            appointmentContactId = Integer.parseInt(String.valueOf(contactIdComboBox.getValue()));
            appointmentCustomerId = Integer.parseInt(String.valueOf(customerIdComboBox.getValue()));
            if (appointmentUserId == null){
                addListenerToComboBox(userIdComboBox, userIdComboBoxErrorLabel);
                errorFlag = true;
            }
            if (appointmentContactId == null){
                addListenerToComboBox(contactIdComboBox, contactIdComboBoxErrorLabel);
                errorFlag = true;
            }
            if (appointmentCustomerId == null){
                addListenerToComboBox(customerIdComboBox, customerIdComboBoxErrorLabel);
                errorFlag = true;
            }
            if (errorFlag == true){
                throw new IllegalArgumentException();
            }
        }
        catch(IllegalArgumentException e){
            return;
        }

        try{
            appointmentStartDatePicker.setStyle(null);
            appointmentStartDate = appointmentStartDatePicker.getValue();
            if (appointmentStartDatePicker.getValue() == null){
                addListenerToLabel(appointmentStartDatePicker, startTimeErrorLabel);
                throw new AppointmentDateException();
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
                appointmentEndDatePicker.setDisable(false);
                appointmentEndDatePicker.setValue(appointmentEndDate);
            }
            catch (AppointmentDateException e){
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
     * This method is overridden because instead of adding a new appointment to the database,
     * it needs to update the previously existing appointment value.
     * @see AddAppointmentController#addAppointment(Appointment)
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @Override
    public void addAppointment(Appointment appointment) throws SQLConnectionDroppedException {
        try{
            String sqlCommand = "UPDATE APPOINTMENTS SET Title= ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

            PreparedStatement stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);

            stmt.setString(1, appointment.getTitle());
            stmt.setString(2, appointment.getDescription());
            stmt.setString(3, appointment.getLocation());
            stmt.setString(4, appointment.getType());
            stmt.setObject(5, LocalDateTime.ofInstant(appointment.getStartDate().toInstant(), businessZoneId));
            stmt.setObject(6, LocalDateTime.ofInstant(appointment.getEndDate().toInstant(), businessZoneId));
            stmt.setObject(7, ZonedDateTime.now());
            stmt.setString(8, CONNECTION_MODEL.getCurrentUsername());
            stmt.setInt(9, appointment.getCustomerID());
            stmt.setInt(10, appointment.getUserID());
            stmt.setInt(11, appointment.getContactID());
            stmt.setInt(12, appointment.getAppointmentID());

            stmt.executeUpdate();
            appointmentEndDatePicker.setDisable(false);
            appointmentEndDatePicker.setValue(appointmentEndDate);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningText"));
            alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("successText"));
            alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("appointmentAddedText") + " " + appointment.getAppointmentID() + ".");
            alert.showAndWait();
            cancelButton.fireEvent(new ActionEvent());
        }
        catch (SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
        }
    }
}
