package com.example.appointment;

import java.time.ZonedDateTime;

/**
 * A class to create a customer object. Specific data is associated with each customer, and one customer is associated with each appointment.
 * */
public class Customer {
    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private ZonedDateTime createDate;
    private String createdBy;
    private ZonedDateTime lastUpdate;
    private String lastUpdatedBy;
    private int divisionID;

    /**
     * A constructor for creating a unique customer object. The customer has specific data for its name and location, and can be tied to a specific appointment.
     * @param customerID The ID value for this customer.
     * @param customerName The name for the customer.
     * @param address The address for the customer. Restricted depending on its country.
     * @param postalCode The postal code for the customer. Restricted depending on its country.
     * @param phone The phone number for the customer. Restricted depending on its country.
     * @param createDate Data for the customer includes the creation date, creation time and time zone.
     * @param createdBy The username that created the customer.
     * @param lastUpdate Data for the customer includes the last updated date, last updated time and time zone.
     * @param lastUpdatedBy The username that last updated the customer.
     * @param divisionID An ID representing the state/province/territory of the customer.
     * */
    public Customer(int customerID, String customerName, String address, String postalCode, String phone, ZonedDateTime createDate,
            String createdBy, ZonedDateTime lastUpdate, String lastUpdatedBy, int divisionID){
            this.customerID = customerID;
            this.customerName = customerName;
            this.address = address;
            this.postalCode = postalCode;
            this.phone = phone;
            this.createDate = createDate;
            this.createdBy = createdBy;
            this.lastUpdate = lastUpdate;
            this.lastUpdatedBy = lastUpdatedBy;
            this.divisionID = divisionID;
        }

    /**
     * @return The ID of the given customer.
     * */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID Takes in an ID for a customer.
     * */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * @return The name of the customer.
     * */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName The name that will be saved for this customer.
     * */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return The address for this customer.
     * */
    public String getAddress() {
        return address;
    }

    /**
     * @param address The address that will be saved for this customer.
     * */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return The phone number for this customer.
     * */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The phone number to be saved for this customer.
     * */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The creation date for this customer.
     * */
    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate The creation data that will be saved for this customer.
     * */
    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * @return The username that created this customer.
     * */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param lastUpdate Passes in the new date information for when this customer was last updated.
     * */
    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return The date information for when this customer was last updated.
     * */
    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return The postal code for this given appointment.
     * */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode The postal code that will be saved for this appointment.
     * */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return The username that last updated this appointment.
     * */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @param lastUpdatedBy Passes in the username that last updated this appointment.
     * */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @return The division ID of the current user.
     * */
    public int getDivisionID(){
        return divisionID;
    }

    /**
     * @param divisionID Passes in the division ID to be saved for this appointment.
     * */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }
}
