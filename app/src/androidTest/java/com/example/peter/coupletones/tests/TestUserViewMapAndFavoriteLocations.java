package com.example.peter.coupletones.tests;


import com.example.peter.coupletones.MyLocation;

import java.util.List;

/**
 * Created by Runjie on 050416.
 */
public class TestUserViewMapAndFavoriteLocations extends
        TestUserTemplate {

    public TestUserViewMapAndFavoriteLocations() {
        super();
    }

    /**
     * Scenario 3
     * In this scenario, instead of checking the marker, check if all the locations is loaded
     * from local storage
     */

    public void testUserSeeFavoriteLocationsOnTheMap() {
        givenIHaveSomeFavoriteLocations();
        whenIOpenTheMap();
        thenISeeFavoriteLocationsOnTheMap();
    }

    /**
     * Scenario 4
     * In this scenario, instead of testing if the user has clicked something, test if the name
     * of the location is correct
     */
    public void testUserViewPropertiesOfLocation() {
        givenIHaveSomeFavoriteLocations();
        whenIOpenTheMap();
        whenIClickOneFavoriteLocation();
        thenISeeThePropertiesOfThatLocation();
    }

    private void whenIClickOneFavoriteLocation() {
        // Randomly select a location
        this.generateRandomNumber();
        currentSelected = myLocationManager.getLocationList()
                .get(randomNumber);
    }

    private void thenISeeThePropertiesOfThatLocation() {
        // Get the list of data to see if they match
        assertTrue(currentSelected.getName()
                .equals(locationArray[randomNumber].getName()));
    }
    private void thenISeeFavoriteLocationsOnTheMap() {
        // Get the list of data to see if they match
        List<MyLocation> list = myLocationManager.getLocationList();
        assertTrue(list.size() == this.locationArray.length);

        for (MyLocation myLocation : this.locationArray) {
            assertTrue(list.contains(myLocation));
        }
    }

    private void whenIOpenTheMap() {
        // Load data
        myLocationManager.updateLocationListFromLocalStorage();
        myLocationManager.updateLocationListFromLocalStorage();
        // Load twice also to detect if it won't add locations already there
    }

}

