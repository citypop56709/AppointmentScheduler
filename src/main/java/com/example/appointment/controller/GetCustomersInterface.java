package com.example.appointment.controller;

import com.example.appointment.Customer;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * An interface that with that allows the program to access customers from the database. This is seperated from the ViewCustomersController,
 * in order to reduce complexity, and improve code re-use.
 * */
public interface GetCustomersInterface extends SQLControllerInterface{
    /**
     * A method that accesses the database to return a list of customers. A statement can be passed in so that it can return all customers,
     * or return customers only for the week. After the customers are collected the database objects are closed.
     * @param stmt A prepared statement that allows the method to get either all or a certain amount of customers.
     * @throws SQLConnectionDroppedException - If a SQL error occurs that disrupts the connection to the database.
     * @return A list of customers.
     * */
    default ObservableList<Customer> getCustomerList(PreparedStatement stmt) throws SQLConnectionDroppedException {
        CachedRowSet crs = null;
        ResultSet rs = null;
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        try {
            rs = stmt.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);

            while (crs.next()){
                int customerID = crs.getInt("Customer_ID");
                String name = crs.getString("Customer_Name");
                String address = crs.getString("Address");
                String postalCode = crs.getString("Postal_Code");
                String phone = crs.getString("Phone");
                LocalDateTime createdDate = (LocalDateTime) crs.getObject("Create_Date");
                String createdBy = crs.getString("Created_By");
                Timestamp lastUpdatedDate = crs.getTimestamp("Last_Update");
                String lastUpdatedBy = crs.getString("Last_Updated_By");
                int divisionId = crs.getInt("Division_ID");
                ZonedDateTime zonedCreatedDate = ZonedDateTime.of(createdDate, ZoneId.systemDefault());
                ZonedDateTime zonedLastUpdatedDate = ZonedDateTime.ofInstant(lastUpdatedDate.toInstant(), ZoneId.systemDefault());
                Customer customer = new Customer(customerID, name, address, postalCode, phone, zonedCreatedDate, createdBy, zonedLastUpdatedDate, lastUpdatedBy, divisionId);
                customers.add(customer);
            }
        }
        catch (SQLException e){
            throw new SQLConnectionDroppedException();
        }
        finally{
            closeSQLObjects(stmt,rs, crs);
        }
        return customers;
    }
}
