package com.example.appointment.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An interface that implements a method to check if a postal code is valid or not.
 * */
public interface PostalCodeCheckInterface{
    /**
     * A method that uses regular expressions to check if a given postal code is in a format valid for its country.
     * <br>
     * Example:
     * <br>
     * U.S. postal code: 12345 OR 12345-1234
     * <br>
     * Canadian postal codes: A1A 1A1
     * <br>
     * U.K. postal codes: SW1A 1AA
     * <br>
     * Postal codes can vary greatly from country to country. This method uses regular expressions to verify if the given postal code string will work for the given country.
     * The algorithm itself is simple, the regular expressions are complicated.
     * <br>
     * For a U.S. postal code it can be in one of two formats, 12345-1234 or 12345. Either format is acceptable. More information can be found <a href="https://en.wikipedia.org/wiki/ZIP_Code">here</a>.
     * <br>
     * Canadian postal codes are in the format of A1A 1A1 with a space between the two groups with no characters in between.
     * There are many specific rules for formatting Canadian postal codes that make the regular expression complex. More information can be found <a href="https://en.wikipedia.org/wiki/Postal_codes_in_Canada">here</a>.
     * <br>
     * U.K. postal codes are in the format of SW1A 1AA. Similar to the Canadian postal codes there are no hyphens with a space in the middle between the two groups. There are fewer restrictions for U.K. postal codes
     * so the regular expression is more complex than the U.S. example but less than the Canadian one. More information can be found <a href="https://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom">here</a>.
     * <br>
     * The method checks to see if there is left over text in case someone inputs a postal code like: 12345$. The $ is an extra character that invalidates the postal code.
     * @param postalCode Takes in a postal code that will be verified.
     * @param country Takes in the name of the country that the postal code originates from.
     * @return True or false depending on if the postal code is valid.
     * */
    default boolean postalCodeChecker (String postalCode, String country){
        if (postalCode.isEmpty()){
            return false;
        }
        postalCode = postalCode.toUpperCase();
        String regex = "([0-9]){5}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(postalCode);
        String leftOverText = postalCode;

        if (country.equals("U.S")){
            if(postalCode.length() == 10){
                regex = "([0-9]){5}([-])([0-9]){4}";
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(postalCode);
            }
            if (matcher.find()){
                postalCode = matcher.group();
            }
            else{
                return false;
            }
        }
        else if (country.equals("Canada")){
            postalCode = postalCode.toUpperCase();
            regex = "^((?![DFIOQUWZ])[A-Z])([0-9A-Z]){1,3}([\\s])([0-9])((?![DFIOQU])[0-9A-Z]){2}";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(postalCode);
            if (matcher.find()){
                postalCode = matcher.group();
            }
            else{
                return false;
            }
        }
        else if (country.equals("UK")){
            postalCode = postalCode.toUpperCase();
            regex = "^([A-Z])([0-9A-Z]){1,3}([\\s])([0-9])([0-9A-Z]){2}";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(postalCode);
            if (matcher.find()){
                postalCode = matcher.group();
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }

        leftOverText = leftOverText.replace(postalCode, "");
        if (leftOverText.equals("")){
            return true;
        }
        return false;
    }
}
