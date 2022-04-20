package com.example.appointment.controller;

import com.example.appointment.ConnectionModel;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javax.sql.rowset.CachedRowSet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An interface that contains methods that are used in all controllers that will directly access the SQL database.
 * */
public interface SQLControllerInterface{

     /**
     * The connection model object that is shared between controllers to maintain access to the database.
     * */
    ConnectionModel CONNECTION_MODEL = ConnectionModel.getInstance();

    /**
     * A method that closes all the SQL objects that are passed in. The point of this method is to reduce copying and pasting
     * because this method gets called every time the database is accessed.
     * @param preparedStatement A PreparedStatement that is passed in to be closed.
    *  @param rs A ResultSet that is passed in to be closed.
     * @param crs A CachedRowSet that is passed in to be closed.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    default void closeSQLObjects(PreparedStatement preparedStatement, ResultSet rs, CachedRowSet crs) throws SQLConnectionDroppedException{
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            CONNECTION_MODEL.SQLAlert();

        }
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            CONNECTION_MODEL.SQLAlert();
        }
        try {
            if (crs != null) {
                crs.close();
            }
        } catch (SQLException e) {
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * @see SQLControllerInterface#closeSQLObjects(PreparedStatement, ResultSet, CachedRowSet)
     * @param preparedStatement A PreparedStatement that is passed in to be closed.
     * @param rs A ResultSet that is passed in to be closed.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    default void closeSQLObjects(PreparedStatement preparedStatement, ResultSet rs) throws SQLConnectionDroppedException{
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            CONNECTION_MODEL.SQLAlert();
        }
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            CONNECTION_MODEL.SQLAlert();
        }
    }

    /**
     * @see SQLControllerInterface#closeSQLObjects(PreparedStatement, ResultSet, CachedRowSet) 
     * @param preparedStatement A PreparedStatement that is passed in to be closed.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    default void closeSQLObjects(PreparedStatement preparedStatement) throws SQLConnectionDroppedException {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            CONNECTION_MODEL.SQLAlert();
        }
    }
}
