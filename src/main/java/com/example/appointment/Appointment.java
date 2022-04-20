package com.example.appointment;

import java.time.*;

/**
 * Creates a unique appointment object with the specified information.
 */
public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int contactID;
    private int customerID;
    private int userID;
    private ZoneId appointmentZoneId;


    /**
     * Constructor that creates a new appointment with information passed in from either the database or the Add Appointment controller.
     * @param appointmentID The ID of the appointment.
     * @param title The title of the appointment
     * @param description A description of the appointment's purpose.
     * @param location The location of the appointment
     * @param contactID The ID of the contact that is associated with this appointment.
     * @param type A description of what type of appointment this is.
     * @param startDate Data for the start of the appointment.
     * @param endDate Data for the end of the appointment.
     * @param customerID The ID for the customer that is associated with this appointment.
     * @param userID The ID for the user that created this appointment.
     * */
    public Appointment(int appointmentID, String title, String description, String location, int contactID,
                       String type, ZonedDateTime startDate, ZonedDateTime endDate, int customerID, int userID){
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactID = contactID;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerID = customerID;
        this.userID = userID;
    }

    /**
     * @return the appointment ID.
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     * @return the title of the appointment.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description of the appointment.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the appointment description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the location of the appointment.
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return The ID of the contact for this appointment.
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @param contactID the contact information of the appointment to be set.
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     * @return the appointment type to set
     */
    public String getType() {
        return type;
    }

    /**
     * @param appointmentType the Appointment type to set
     */
    public void setType(String appointmentType) {
        this.type = appointmentType;
    }

    /**
     * @return the start date of the appointment.
     */
    public ZonedDateTime getStartDate() {
        return startDate;
    }

    /**
     * @return the end date of the appointment.
     */
    public ZonedDateTime getEndDate() {
        return endDate;
    }


    /**
     * A method that lets the program change the zone ID to the one passed in. This is useful for switching
     * from business ZoneId of Eastern Standard Time to the user's local Zone ID.
     * @param zoneId Takes in the zone ID set by the program. This can be used to change to and from Eastern Standard Time.
     * */
    public void setAppointmentZoneId(ZoneId zoneId){
        appointmentZoneId = zoneId;
    }

    /**
     * @return the use ID of the appointment.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the user ID to be set.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return The ID of the customer for this appointment.
     * */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID The customer ID to set for this appointment.
     * */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
}
