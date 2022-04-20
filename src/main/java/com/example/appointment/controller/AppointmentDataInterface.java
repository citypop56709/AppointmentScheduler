package com.example.appointment.controller;

import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An interface that is used to load data for the Add Appointment, and Modify Appointment controllers.
 * This interface exists to compartmentalize some of the more technical code from the controller, so the controller can be more tailored to a specific need.
 * */
public interface AppointmentDataInterface extends SQLControllerInterface{

    /**
     * A method that creates a list of contact IDs from the database. This enables the ComboBox in the
     * controller to have an up-to-date list of values.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @return A list of contact IDs from the database.
     * */
    default List<Integer> getContactIdList() throws SQLConnectionDroppedException {
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
     * A method that creates a list of customers IDs from the database. This enables the ComboBox in the
     * controller to have an up-to-date list of values.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @return A list of customer IDs from the database.
     * */
    default List<Integer> getCustomerIdList() throws SQLConnectionDroppedException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        CachedRowSet crs = null;
        List<Integer> customers = new ArrayList<>();
        try {
            String sqlCommand = "SELECT Customer_ID FROM CUSTOMERS";
            preparedStatement = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);
            rs = preparedStatement.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while(crs.next()){
                customers.add(crs.getInt(1));
            }
        } catch (SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
        }
        finally{
            closeSQLObjects(preparedStatement, rs, crs);
        }
        return customers;
    }

    /**
     * A method that creates a list of user IDs from the database. This enables the ComboBox in the
     * controller to have an up-to-date list of values.
 * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * @return A list of customer IDs from the database.
     * */
    default List<Integer> getUserIdList() throws SQLConnectionDroppedException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        CachedRowSet crs = null;
        List<Integer> users = new ArrayList<>();
        try {
            String sqlCommand = "SELECT User_ID FROM USERS";
            preparedStatement = CONNECTION_MODEL.getConnection().prepareStatement(sqlCommand);
            rs = preparedStatement.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while(crs.next()){
                users.add(crs.getInt(1));
            }
        } catch (SQLException | IOException e) {
            CONNECTION_MODEL.SQLAlert();
        }
        finally{
            closeSQLObjects(preparedStatement, rs, crs);
        }
        return users;
    }
}
