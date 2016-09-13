package com.example.peter.coupletones.tests;

/**
 * Testing ZZZN-30
 * Created by Anoxic on 060216.
 */
public class TestUserRemovesAFavoriteLocation extends TestUserTemplate {

    public TestUserRemovesAFavoriteLocation() {
        super();
    }

    /**
     * Scenario 1
     * In this scenario, instead of testing if the location has been removed from the map, test
     * if we can find it in the location list
     */

    public void testUserClickRemoveToConfirmRemoval() {
        givenIHaveSomeFavoriteLocations();
        whenIPressRemoveOnALocation();
        thenISeeLocationIsRemovedFromTheMap();
    }

    private void thenISeeLocationIsRemovedFromTheMap() {
        assertTrue(myLocationManager.getLocationList()
                .indexOf(currentSelected) == -1);
    }

    private void whenIPressRemoveOnALocation() {
        this.generateRandomNumber();
        currentSelected = myLocationManager.getLocationList()
                .get(randomNumber);

        myLocationManager.removeLocation(currentSelected);
    }

}
