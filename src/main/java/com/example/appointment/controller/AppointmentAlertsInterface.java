package com.example.appointment.controller;

import com.example.appointment.Appointment;
import javafx.scene.control.Alert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * An interface that is used to create alerts for if an appointment is expiring within 15 minutes of login.
 * */
public interface AppointmentAlertsInterface extends SQLControllerInterface{
    /**
     * A method that creates a JavaFX alert if a user has appointments within 15 minutes of login.
     * This method has two main parts:
     * <ol>
     *     <li>If they are no appointments within 15 minutes the user is informed with an alert saying as such.</li>
     *     <li>If they are appointments within 15 minutes </li>
     * </ol>
     * If the appointments are within 15 minutes a list is created using a StringBuilder. This StringBuilder takes in the appointment ID,
     * title, start time, and end time for each appointment. This enables each appointment to be displayed in a single alert message instead
     * of bombarding the user with multiple alerts. The appointment's time is formatted to LocalDateTime so that it will be adjusted to the user's time zone.
     * @param appointments Passes in a list of appointments.
     * */
    default void appointmentAlert(ArrayList<Appointment> appointments){
        DateTimeFormatter hourTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        ArrayList<Appointment> expiringAppointments = new ArrayList<>(getExpiringAppointments(appointments));
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(CONNECTION_MODEL.getResourceBundle().getString("warningAlert"));
        alert.setHeaderText(CONNECTION_MODEL.getResourceBundle().getString("appointmentWarningAlertHeaderText"));
        alert.setResizable(true);
        if (expiringAppointments.isEmpty()){
            alert.setContentText(CONNECTION_MODEL.getResourceBundle().getString("noAppointmentsInFifteenText"));
            alert.show();
            return;
        }
        StringBuilder alertContentText = new StringBuilder(CONNECTION_MODEL.getResourceBundle().getString("appointmentWarningContentText"));
        alertContentText.append("\n");
        for (Appointment appointment : expiringAppointments){
            String idText = CONNECTION_MODEL.getResourceBundle().getString("appointmentIDTextLabel");
            String titleText = CONNECTION_MODEL.getResourceBundle().getString("appointmentTitleTextLabel");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String appointmentDateFormatted = appointment.getStartDate().format(formatter);
            String appointmentStartDateText = CONNECTION_MODEL.getResourceBundle().getString("appointmentStartDateTextLabel");
            String startTimeText = CONNECTION_MODEL.getResourceBundle().getString("appointmentStartTimeTextLabel");
            String endTimeText = CONNECTION_MODEL.getResourceBundle().getString("appointmentEndTimeTextLabel");
            alertContentText.append("\n");
            String appointmentText = idText + ": " + appointment.getAppointmentID() + " " + titleText + ": " + appointment.getTitle()
                    + "\n" + appointmentStartDateText + ":" + " " +appointmentDateFormatted + " " + startTimeText + ": "
                    + LocalDateTime.ofInstant(appointment.getStartDate().toInstant(), ZoneId.systemDefault()).format(hourTimeFormatter) + " "
                    + endTimeText + ": " + LocalDateTime.ofInstant(appointment.getEndDate().toInstant(), ZoneId.systemDefault()).format(hourTimeFormatter);
            alertContentText.append(appointmentText);
        }
        alert.setContentText(alertContentText.toString());
        alert.show();
    }

    /**
     * A method that creates a list of expiring appointments from the list of total user appointments.
     * It goes through each appointment adds it to the list if the appointment is after the current time, and expires within
     * 15 minutes of the current time.
     * @param appointments A list of appointments.
     * @return A list of a list of appointments expiring within 15 minutes of login.
     * */
    default ArrayList<Appointment> getExpiringAppointments(ArrayList<Appointment> appointments) {
        ArrayList<Appointment> expiringAppointments = new ArrayList<>();
        for (Appointment appointment : appointments){
            if ((appointment.getStartDate().isBefore(ZonedDateTime.now().plusMinutes(15)) && appointment.getStartDate().isAfter(ZonedDateTime.now().minusMinutes(15)))
                 || (appointment.getEndDate().isAfter(ZonedDateTime.now()) && appointment.getStartDate().isBefore(ZonedDateTime.now()))){
                expiringAppointments.add(appointment);
                }
            }
        return expiringAppointments;
    }
}
