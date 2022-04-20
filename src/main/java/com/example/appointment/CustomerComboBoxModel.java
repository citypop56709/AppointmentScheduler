package com.example.appointment;

import com.example.appointment.controller.SQLControllerInterface;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

/**
 * A Singleton class that manages the combo boxes for the customer controllers enabling the combo box on the two customer scenes to have accurate Country and Division data.
 * */
public class CustomerComboBoxModel implements SQLControllerInterface {
    private static CustomerComboBoxModel INSTANCE;
    private static HashMap<Integer, String> countries;
    private static HashMap<Integer, String> divisions;
    private PreparedStatement preparedStatement;
    private CachedRowSet crs;
    private ResultSet rs;
    private final ConnectionModel connectionModel;

    /**
     * A constructor method that creates a CustomerComboBoxModel object. It is private because there should only be one object of this type at once.
     * @param connectionModel Passes in the connection model object into the customer combo box class to enable database connectivity,
     *                        and language features.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     * */
    private CustomerComboBoxModel(ConnectionModel connectionModel) throws SQLConnectionDroppedException {
        this.connectionModel = connectionModel;
        setCountriesComboBoxHashMap();
    }

    /**
     * A method that returns an instance of the CustomerComboBoxModel.
     * @param connectionModel Passes in the connection model object into the customer combo box class to enable database connectivity,
     *                              and language features.
     * @return An instance of the CustomerComboBoxModel. If one does not already exist it will make a new object.
     * @throws IOException If an input/output error occurs.
     * @throws SQLException SQLException If a SQL database error occurs.
     * */
    public static CustomerComboBoxModel getInstance(ConnectionModel connectionModel) throws IOException, SQLException {
        if (INSTANCE == null){
            INSTANCE = new CustomerComboBoxModel(connectionModel);
        }
        return INSTANCE;
    }

    /**
     * A method that sets up a HashMap that stores the country ID's as a key, and a list of the country's states or provinces as a value.
     * It retrieves this information from the database.
     * @throws SQLConnectionDroppedException If a SQL error occurs that disrupts the connection to the database.
     */
    public void setCountriesComboBoxHashMap() throws SQLConnectionDroppedException {
        try {
            if (ConnectionModel.getInstance().getConnection() == null) {
                throw new SQLException("The countries cannot be loaded until the connection is established.");
            }
            countries = new HashMap<>();
            String query = "SELECT Country_ID, Country FROM COUNTRIES";
            preparedStatement = ConnectionModel.getInstance().getConnection().prepareStatement(query);
            rs = preparedStatement.executeQuery(query);
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);

            while (crs.next()) {
                int countryID = crs.getInt(1);
                String country = crs.getString(2);
                countries.put(countryID, country);
            }
        }
        catch (SQLException | IOException e){
            connectionModel.SQLAlert();
        }
        finally{
            closeSQLObjects(preparedStatement, rs, crs);
        }
    }

    /**
     * A method that returns the HashMap of all the available countries.
     * @return A HashMap of all the countries that are available from the database.
     */
    public HashMap<Integer, String> getCountriesComboBoxHashMap(){
        return countries;
    }

    /**
     * A method that connects to the database and sets up the division combo box so that all of its divisions are appropriate for the given country.
     * For example if you select the United States, you should get a combo box listing US states. If you select Canada,
     * you should get a selection of provinces that are actually in Canada.
     * @param countryID Passes in the ID of the selected country.
     * @throws SQLException If a database error occurs.
     * */
    public void setDivisionsComboBoxHashMap(int countryID) throws SQLException{
        try {
            if (ConnectionModel.getInstance().getConnection() == null || countries == null) {
                throw new SQLException("The divisions cannot be loaded until the connection is established, and the countries HashMap is loaded.");
            }

            divisions = new HashMap<>();
            String query = "SELECT Division_ID, FIRST_LEVEL_DIVISIONS.Division FROM FIRST_LEVEL_DIVISIONS WHERE Country_ID = ?";
            preparedStatement = ConnectionModel.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(countryID));
            rs = preparedStatement.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            while (crs.next()) {
                divisions.put(crs.getInt(1), crs.getString(2));
            }
        }
        catch (SQLException | IOException e){
            connectionModel.SQLAlert();
        }
        finally{
            closeSQLObjects(preparedStatement, rs, crs);
        }
    }

    /**
     * A method that returns a HashMap of all the available divisions.
     * @return A HashMap of available divisions.
     * */
    public HashMap<Integer, String> getDivisionsComboBoxHashMap() {
        return divisions;
    }
}
