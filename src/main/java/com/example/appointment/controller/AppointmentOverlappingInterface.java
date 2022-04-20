package com.example.appointment.controller;

import com.example.appointment.Appointment;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public interface AppointmentOverlappingInterface extends GetAppointmentsInterface{
    /**
     * A method that creates an alert message that tells the user if a customer has appointments that overlap.
     * @param appointments A list of overlapping appointments to add to the alert.
     * */
    default void appointmentOverlapAlert(List<Appointment> appointments){
        DateTimeFormatter hourTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningText"));
        alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("overlappingAppointmentsHeaderText"));
        StringBuilder alertContentText = new StringBuilder(CONNECTION_MODEL.getResourceBundle().getString("overlappingAppointmentsContentText"));
        alertContentText.append("\n");
        for (Appointment appointment : appointments){
            String idText = CONNECTION_MODEL.getResourceBundle().getString("appointmentIDTextLabel");
            String titleText = CONNECTION_MODEL.getResourceBundle().getString("appointmentTitleTextLabel");
            String startTimeText = CONNECTION_MODEL.getResourceBundle().getString("appointmentStartTimeTextLabel");
            String endTimeText = CONNECTION_MODEL.getResourceBundle().getString("appointmentEndTimeTextLabel");
            alertContentText.append("\n");
            String appointmentText = idText + ": " + appointment.getAppointmentID() + " " + titleText + ": " + appointment.getTitle()
                    + "\n" + startTimeText + ": " + LocalDateTime.ofInstant(appointment.getStartDate().toInstant(), ZoneId.systemDefault()).format(hourTimeFormatter) + " "
                    + endTimeText + ": " + LocalDateTime.ofInstant(appointment.getEndDate().toInstant(), ZoneId.systemDefault()).format(hourTimeFormatter);
            alertContentText.append(appointmentText);
        }
        alert.setContentText(alertContentText.toString());
        alert.showAndWait();
    }

    /**
     * A method that returns a list of appointments that are overlapping for a given customer. This list can be used to create an alert using the appointment overlap method.
     * <em>Authors Note</em>
     * <br>
     * This method and was surprisingly difficult to design. When I first made it the appointment times were all wrong because they weren't changing time with the time zones.
     * I figured out to get it to properly work I had to convert the local times to Instant, and then to ZonedDateTimes to be compared.
     * @param customerId The customer ID of the customer whose appointments that need to be checked.
     * @param userAppointmentStartDate The start date and time that the user has entered into the program that will be checked to see if it's overlapping.
     * @param userAppointmentEndDate The end date and time that the user has entered into the program that will be checked to see if it's overlapping.
     * @param userAppointmentId The ID of the appointment that is getting checked. This is necessary to prevent the method from saying an appointment overlaps with itself when being modified.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException If a SQL database error occurs.
     * @return A list of appointments that are overlapping.
     * */
    default List<Appointment> isAppointmentOverlapping(int customerId, ZonedDateTime userAppointmentStartDate, ZonedDateTime userAppointmentEndDate, int userAppointmentId) throws IOException, SQLException {
        List<Appointment> overlappingAppointments = new ArrayList<>();
        String sqlQuery = "SELECT * FROM APPOINTMENTS WHERE Customer_ID = ?";
        PreparedStatement stmt = CONNECTION_MODEL.getConnection().prepareStatement(sqlQuery);
        stmt.setInt(1, customerId);
        ArrayList<Appointment> customerAppointments = (ArrayList<Appointment>) getAppointmentList(stmt);
        for (Appointment customerAppointment : customerAppointments) {
            if (customerAppointment.getAppointmentID() != userAppointmentId){
                if (customerAppointment.getStartDate().getDayOfYear() == (userAppointmentStartDate.getDayOfYear())) {
                    if (isBetween(customerAppointment.getStartDate(), customerAppointment.getEndDate(), userAppointmentStartDate, userAppointmentEndDate)){
                        overlappingAppointments.add(customerAppointment);
                    }
                }
            }
        }
        return overlappingAppointments;
    }

    default boolean isBetween(ZonedDateTime zonedStartTime, ZonedDateTime zonedEndTime, ZonedDateTime newStartTime, ZonedDateTime newEndTime){
        if (newStartTime.isAfter(zonedStartTime) || newStartTime.isEqual(zonedStartTime)){
            if ((newStartTime.isBefore(zonedEndTime) || newStartTime.isEqual(zonedEndTime)) || (newEndTime.isBefore(zonedEndTime) || newEndTime.isEqual(zonedEndTime))) {
                return true;
            }
        }
        else{
            if(zonedEndTime.isBefore(newEndTime) || newEndTime.isEqual(zonedEndTime)){
                return true;
            }
        }
        return false;
    }
}
