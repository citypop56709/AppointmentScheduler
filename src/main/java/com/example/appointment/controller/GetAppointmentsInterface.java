package com.example.appointment.controller;

import com.example.appointment.Appointment;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.scene.control.Alert;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An interface that with that allows the program to access appointments from the database. This is seperated from the MainController,
 * in order to reduce complexity, and improve code re-use.
 * */
public interface GetAppointmentsInterface extends SQLControllerInterface{
    /**
     * A method that accesses the database to return a list of appointments. A statement can be passed in so that it can return all appointments,
     * or return appointments only for the week. After the appointments are collected the database objects are closed.
     * @param stmt A prepared statement that allows the method to get either all appointments, or appointments only within a specific amount of time.
     * @return A list of appointments.
     * @throws SQLConnectionDroppedException - If a SQL error occurs that disrupts the connection to the database.
     * */
    default List<Appointment> getAppointmentList(PreparedStatement stmt) throws SQLConnectionDroppedException {
        List<Appointment> appointments= new ArrayList<>();
        CachedRowSet crs = null;
        ResultSet rs = null;
        try{
            rs = stmt.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while (crs.next()){
                int appointmentID = crs.getInt("Appointment_ID");
                String title = crs.getString("Title");
                String description = crs.getString("Description");
                String location = crs.getString("Location");
                String type = crs.getString("Type");
                ZonedDateTime startDate = ZonedDateTime.of((LocalDateTime)crs.getObject("Start"), ZoneId.of("America/New_York"));
                ZonedDateTime endDate = ZonedDateTime.of((LocalDateTime) crs.getObject("End"), ZoneId.of("America/New_York"));
                int contactID = crs.getInt("Contact_ID");
                int customerID = crs.getInt("Customer_ID");
                int appointmentUserID = crs.getInt("User_ID");
                Appointment appointment = new Appointment(appointmentID, title, description, location, contactID, type,
                        startDate, endDate, customerID, appointmentUserID);
                appointments.add(appointment);
            }
        }
        catch (SQLException e) {
            throw new SQLConnectionDroppedException();
        }
        finally{
            closeSQLObjects(stmt, rs, crs);
        }
        return appointments;
    }
}
