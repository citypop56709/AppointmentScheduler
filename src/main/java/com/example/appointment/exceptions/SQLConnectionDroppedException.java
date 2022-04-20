package com.example.appointment.exceptions;

import java.sql.SQLException;

/**
 * An exception that is designed to be thrown if the connection to the SQL database is dropped.
 * */
public class SQLConnectionDroppedException extends SQLException{

    /**
     * Constructs a SQLConnectionDroppedException object if the exception is thrown.
     * */
    public SQLConnectionDroppedException(){
        super();
    }
}
