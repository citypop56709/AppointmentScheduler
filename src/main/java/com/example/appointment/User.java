package com.example.appointment;

import javafx.collections.ObservableList;

/**
 * A class that creates a user object that has its own appointments. It also has methods that allow these appointments to be manipulated in some way.
 * */
public class User {
    private final ObservableList<Appointment> userAppointments;

    /**
     * Constructor that creates a user object that contains an observable list of appointments.
     * @param appointments Passes in an observable list of appointments.
     * */
    public User(ObservableList<Appointment> appointments){
        userAppointments = appointments;
    }

    /**
     * Getter that returns an observable list of this user's appointments.
     * @return an observable list of the user's appointments.
     * */
    public ObservableList<Appointment> getAppointments() {
        return userAppointments;
    }

    /**
     * Adds a new appointment the user's observable list.
     * @param appointment Takes in an appointment object.
     * */
    public void addAppointment(Appointment appointment) {
        userAppointments.add(appointment);
    }

    /**
     *  Removes an appointment from the user's observable list.
     * @param appointment Takes in an appointment object.
     * */
    public void removeAppointment(Appointment appointment){
        userAppointments.remove(appointment);
    }
}
