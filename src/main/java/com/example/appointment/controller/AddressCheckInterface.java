package com.example.appointment.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An interface implements a method to check if an address is valid or not.
 * */
public interface AddressCheckInterface{
    /**
     * A method that uses regular expressions to check if the address format is valid based on the number of fields.
     * <br>
     * Example:
     * <br>
     * U.S. address: 123 ABC Street, White Plains
     * <br>
     * Canadian address: 123 ABC Street, Newmarket
     * <br>
     * U.K. address: 123 ABC Street, Greenwich, London
     * <br>
     * American and Canadian addresses have two fields, not including the street address, the street name, and the city name.
     * <br>
     * This is in contrast to the U.K. address which has three fields.
     * <br>
     * If the users address is empty the method returns false.
     * <br>
     * If the street number is not recognized by the regular expression "^([\S]+[0-9])" then the method returns false.
     * <br>
     * After the street number is checked the fieldCheck method is called in order to parse the other fields in the address.
     * <br>
     * If the fields match the given field count then the method returns true. Otherwise if the fieldCheck returns -1 then the method outputs false.
     * @param address Takes in the address to get checked.
     * @param fieldCount Takes in the number of fields that the address should have.
     * @return Returns true or false depending on if the address is valid.
     * */
    default boolean addressCheck(String address, int fieldCount) {
        if (!address.isEmpty()) {
            String streetNumber;
            String regex = "^([\\S]+[0-9])";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(address);
            if (matcher.find()){
                streetNumber = matcher.group();
                address = (address.substring(streetNumber.length()).trim());
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
        int fieldCheckValue = fieldCheck(address);
        if (fieldCheckValue == -1) {
            return false;
        }
        return (fieldCheckValue == fieldCount);
    }

    /**
     * A method that uses regular expressions to count the number of address fields.
     * The address string will be parsed using the [^,]* regular expression which will find all text except for commas.
     * If the string has extra text that it should not have such as an extra comma it will be caught under the extra text if statement and the counter will return -1.
     * At the end of its parsing the fieldCheck method returns a count of how many fields it found that are valid.
     * @param address Takes in the address to be verified.
     * @return a count of the number of fields in passed in address.
     * */
    private int fieldCheck(String address){
        int counter = 0;
        if (!address.isEmpty()){
            int endIndex = 0;
            String regex = "[^,]*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(address);
            while (matcher.find()){
                if (!matcher.group().isEmpty()){
                    endIndex = matcher.end();
                    counter++;
                }
            }
            String extraText = "";
            extraText = address.substring(endIndex);
            if (!extraText.isEmpty()){
                counter = -1;
            }
        }
        return counter;
    }
}
