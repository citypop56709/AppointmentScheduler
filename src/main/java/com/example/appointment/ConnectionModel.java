package com.example.appointment;

import com.example.appointment.controller.SQLControllerInterface;
import com.example.appointment.exceptions.SQLConnectionDroppedException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;

/**
 * A Singleton class that manages the database connection, and tracks the current language for the entire application.
 * Password is stored as character array in order to improve security so that the password String is never saved into the String pool.
 * */
public final class ConnectionModel implements SQLControllerInterface {
    private static ConnectionModel INSTANCE;
    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;
    private ResourceBundle resourceBundle;
    private Locale userLocale;
    private Integer userId = null;
    private String currentUsername = "";
    private static int retryCount = 0;
    private final Logger LOGGER = Logger.getLogger(ConnectionModel.class.getName());

    /**
     * Constructor that creates a ConnectionModel object. It is private so that only one ConnectionModel object is ever created.
     * @throws IOException If an input/output error occurs.
     * */
    private ConnectionModel() throws IOException {
        DriverManager.setLoginTimeout(3);
        userLocale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("MessagesBundle", userLocale);
        setDatabaseLog();
    }

    /**
     * A method that returns an object that contains the current time.
     * @return ZonedDateTime - A ZonedDateTime object of the current time and ZoneID.
     * */
    public ZonedDateTime getCurrentTime(){
        return ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    }

    /**
     * A method that returns an instance of the ConnectionModel object. It only creates a new
     * @return a ConnectionModel object
     * */
    public static ConnectionModel getInstance(){
        try{
            if (INSTANCE == null){
                INSTANCE = new ConnectionModel();
            }
            return INSTANCE;
        }
        catch (IOException e){
            Platform.exit();
        }
        return INSTANCE;
    }

    /**
     * A method that establishes the SQL database connection. If the database cannot be contacted then it will call itself 3 times. If after the 3rd time there is still not a connection,
     * then an error message wil be generated and an exception will be thrown.
     * @param username - The username that the user passes in through the login page.
     * @param password - The password that the user passes in through the login page.
     * @throws SQLException If the SQL database cannot be contacted.
     * @throws SQLConnectionDroppedException If the SQL connection is dropped and cannot be re-established.
     * */
    public void establishConnection(String username, char[] password) throws SQLException, SQLConnectionDroppedException {
        currentUsername = username;
        String databaseURL = "jdbc:mysql://localhost:3306/client_schedule?user=sqlUser&password=Passw0rd!";
        try {
            this.conn = DriverManager.getConnection(databaseURL);
            setUserId(username, password);
            LOGGER.info( "Access approved." + currentUsername + "." + getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "." + ZoneId.systemDefault());
        }
        catch (SQLException e){
            retryCount++;
            if (retryCount < 3){
                establishConnection(username, password);
            }
            else{
                LOGGER.warning("Access denied." + currentUsername + "." + getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ".");
                throw new SQLConnectionDroppedException();
            }
        }
    }

    /**
     * A method that returns the connection object that is established with the establishConnection() method.
     * @throws IOException If an input/output error occurs.
     * @return A SQL connection object.
     * */
    public Connection getConnection() throws IOException {
        if (conn == null){
            throw new IOException("You cannot access a connection when it is not instantiated.");
        }
        return conn;
    }

    /**
     * A method that sets up the database logging system so that a text document named 'log_activity.txt' can be created that tracks login attempts.
     * The log is limited to only 1 file at a time, at a max size of 3000 MBs.
     * @throws IOException If an input/output error occurs.
     * */
    private void setDatabaseLog() throws IOException {
        LogManager.getLogManager().reset();
        LOGGER.setLevel(Level.INFO);
        FileHandler fileHandler = new FileHandler("log_activity.txt", 3000000, 1, true);
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
        LOGGER.addHandler(fileHandler);
    }

    /**
     * Displays an alert window with the text for a SQL error message in case one occurs. This is seperated into its own method in order to improve code reuse.
     * @throws SQLConnectionDroppedException If the SQL connection is dropped and cannot be re-established.
     * */
    public void SQLAlert() throws SQLConnectionDroppedException{
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(resourceBundle.getString("warningAlert"));
        alert.setHeaderText(resourceBundle.getString("warningAlertSQLHeaderText"));
        alert.setContentText(resourceBundle.getString("warningAlertSQLContextText"));
        alert.showAndWait();
        throw new SQLConnectionDroppedException();
    }

    /**
     * A method that returns the username of the current user.
     * @return A string containing the current customer name.
     * */
    public String getCurrentUsername(){
        return currentUsername;
    }

    /**
     * A method that sets the user ID based on the username that is passed into the login page.
     * @param username - The username that the user passes in the login page.
     * @param password - The password that the user passes in the login page.
     * @throws SQLConnectionDroppedException - If the SQL connection is dropped and cannot be re-established.
     * */
    public void setUserId(String username, char[] password) throws SQLConnectionDroppedException{
        if (username != null){
            try{
                userId = -1;
                String query = "SELECT User_ID FROM USERS WHERE User_Name = ? AND password = ?";
                stmt = CONNECTION_MODEL.getConnection().prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, String.valueOf(password));
                rs = stmt.executeQuery();
                while(rs.next()){
                    userId = rs.getInt("User_ID");
                }
                if (userId == -1){
                    throw new SQLException();
                }
            }
            catch (SQLException | IOException e){
               throw new SQLConnectionDroppedException();
            }
            finally{
                closeSQLObjects(stmt, rs);
            }
        }
    }

    /**
     * A method that returns the user ID of the currently logged in user.
     * @return the current user's user ID.
     * */
    public Integer getUserId(){
        return userId;
    }

    /**
     * A method that returns the currently used resource bundle. This is essentially for allowing the program to change languages on the fly.
     * @return the currently used resource bundle.
     * */
    public ResourceBundle getResourceBundle(){
        return resourceBundle;
    }

    /**
     * A method that calls all the language translations methods to make it simpler to translate everything in the application.
     * Because this class is a Singleton, this change will be reflected across every scene.
     * @param locale takes in the user's default Locale.
     * */
    public void setCurrentLocale(Locale locale) {
        this.userLocale = locale;
        if (locale.getLanguage().startsWith("fr")){
            resourceBundle = ResourceBundle.getBundle("MessagesBundle_FR");
        }
        else{
            resourceBundle = ResourceBundle.getBundle("MessagesBundle_en_US");
        }
    }

    /**
     * A method that returns the current user's selected locale.
     * @return the currently selected user locale.
     * */
    public Locale getCurrentLocale(){
        return userLocale;
    }
}
