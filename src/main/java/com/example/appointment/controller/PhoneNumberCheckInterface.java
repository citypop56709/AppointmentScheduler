package com.example.appointment.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An interface that implements a method to check if a phone number is valid or not.
 * */
public interface PhoneNumberCheckInterface {
    /**
     * A method that uses regular expressions to check if the phone number format is valid for the country the phone number originates from.
     * <br>
     * Example:
     * <br>
     * U.S. phone number: 817-555-5555
     * <br>
     * Canadian phone number: 416-555-5555
     * <br>
     * U.K. phone number: 020 7183 87500 OR +442071838750
     * <br>
     * If the phone number is empty the method will automatically return false.
     * <br>
     * The United States and Canada are both part of the North American Numbering Plan (NANP), so they accept phone numbers in the same format.
     * More information can be found <a href="https://en.wikipedia.org/wiki/North_American_Numbering_Plan">here</a>.
     * <br>
     * United Kingdom phone numbers can be given in either the standard format according ot their National Telephone Numbering System,
     * or they can be given in the international E.164 format.
     * More information can be found <a href="https://en.wikipedia.org/wiki/Telephone_numbers_in_the_United_Kingdom">here</a>.
     * <br>
     * The method can check what format the phone number is and then check the appropriate regular expression for each one.
     * <br>
     * If the phone number is not in the correct format for either country then the method will immediately output false.
     * @param phoneNumber Takes in the phone number to be verified.
     * @param country Takes in the name of the country that the phone number originates from.
     * @return Returns true or false depending on if the phone number is valid.
     * */
    default boolean phoneNumberCheck(String phoneNumber, String country){
        if (phoneNumber.isEmpty()){
            return false;
        }

        if(country.equals("U.S") || country.equals("Canada")){
            String regex = "([0-9]){10}";
            Pattern pattern = Pattern.compile(regex);
            for (int i = 0; i < phoneNumber.length();){
                if (('-' == phoneNumber.charAt(i)) || (' ' == phoneNumber.charAt(i))) {
                    phoneNumber = phoneNumber.replace(String.valueOf(phoneNumber.charAt(i)), "");
                }
                i += 3;
            }

            String leftOverText = phoneNumber;
            Matcher matcher = pattern.matcher(phoneNumber);
            if (matcher.find()){
                phoneNumber = matcher.group();
            }
            else{
                return false;
            }
            leftOverText = leftOverText.replace(phoneNumber, "");
            if (leftOverText.isEmpty()){
                try{
                    Long.valueOf(phoneNumber);
                    return true;
                }
                catch(NumberFormatException e){
                    return false;
                }
            }
            else{
                return false;
            }
        }

        else if (country.equals("UK")){
            String regex = "([0-9]){11}";
            if (phoneNumber.startsWith("+44")){
                phoneNumber = phoneNumber.replace("+44", "");
                regex = "([0-9]){9}";
            }

            for (int i = 0; i < phoneNumber.length(); i++){
                if ((' ' == phoneNumber.charAt(i))){
                    phoneNumber = phoneNumber.replace(String.valueOf(phoneNumber.charAt(i)), "");
                }
            }

            String leftOverText = phoneNumber;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(phoneNumber);
            if (matcher.find()){
                phoneNumber = matcher.group();
            }
            else{
                return false;
            }
            leftOverText = leftOverText.replace(phoneNumber, "");
            if (leftOverText.isEmpty()){
                try{
                    Long value = Long.valueOf(phoneNumber);
                    return true;
                }
                catch(NumberFormatException e){
                    return false;
                }
            }
        }
        return false;
    }
}
