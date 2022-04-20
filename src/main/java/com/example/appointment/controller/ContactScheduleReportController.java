package com.example.appointment.controller;

import com.example.appointment.Appointment;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A controller that loads the Contact reports scene. This scene enables users to select  and retrieve how many appointments
 * that customer has.
 * */
public class ContactScheduleReportController extends ReportsController implements GetAppointmentsInterface, ComboBoxListenerInterface{
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;
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
    private TableView.TableViewSelectionModel<Appointment> selectionAppointmentModel;
    @FXML
    private ComboBox<Integer> contactIdComboBox;
    @FXML
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private PreparedStatement stmt;

    /**
     * A method that loads the contact ID ComboBox with values from the database.
     * After the ComboBox gets it values from the setComboBoxValidation method it sorts the values in numerical order.
     * @throws SQLException If a SQL database error occurs.
     * @throws IOException If an input/output error occurs.
     * */
    @FXML
    public void setContactIdComboBox() throws SQLException, IOException {
        ObservableList<Integer> observableContactIds = FXCollections.observableList(getContactIdList());
        Collections.sort(observableContactIds);
        this.setIntegerComboBoxValidation(contactIdComboBox, observableContactIds);
        contactIdComboBox.setOnAction(actionEvent ->
                {
                    try {
                        setTableView(Integer.parseInt(String.valueOf(contactIdComboBox.getValue())));
                    } catch (SQLConnectionDroppedException e) {
                        changeSceneToLoginController(actionEvent);
                    }
                }
            );
    }

    /**
     * A method that retrieves a list of contact ID values from the database. These values can be used to check which contacts have appointments.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @return A list of contact IDs.
     * */
    public List<Integer> getContactIdList() throws SQLConnectionDroppedException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        CachedRowSet crs = null;
        List<Integer> contacts = new ArrayList<>();
        try {
            String sqlCommand = "SELECT Contact_ID FROM CONTACTS";
            preparedStatement = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);
            rs = preparedStatement.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while(crs.next()){
                contacts.add(crs.getInt(1));
            }
        } catch (SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
        }
        finally{
            closeSQLObjects(preparedStatement, rs, crs);
        }
        return contacts;
    }

    /**
     * <br>
     * A method that sets up the TableView object by retrieving appointment objects from the database based on the select contact ID, placing them into a user object's
     * appointment list, and then displaying that list in the table.
     * <br>
     * They are four columns that have lambda expressions in this method
     *     <ol>
     *         <li>appointmentStartTimeColumn</li>
     *         <li>appointmentEndTimeColumn</li>
     *         <li>startDateColumn</li>
     *         <li>endDateColumn</li>
     *     </ol>
     *     <br>
     *     The two time columns work by formatting the appointment date objects into the time format HH:mm for each cell in the column.
     *     The two date columns work the same way and format each appointment date object to the format of MM-dd-yyyy.
     *     These lambda expressions are an easy way to make the time and date information readable and still convertible with
     *     the actual appointment object in the database.
     * @param contactId The ID of the contact that is passed in to the SQL query.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    @FXML
    public void setTableView(int contactId) throws SQLConnectionDroppedException {
        try{
            String sqlQuery = "SELECT * FROM APPOINTMENTS WHERE Contact_ID = ?";
            stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlQuery);
            stmt.setInt(1, contactId);
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
            appointmentTableView.setPlaceholder(new Label(CONNECTION_MODEL.getResourceBundle().getString("tableSetAppointmentsPlaceHolderText")));
            selectionAppointmentModel = appointmentTableView.getSelectionModel();
            selectionAppointmentModel.setSelectionMode(SelectionMode.SINGLE);
        }
        catch (SQLException | IOException e){
            CONNECTION_MODEL.SQLAlert();
        }
        finally{
            closeSQLObjects(stmt);
        }
    }
}
