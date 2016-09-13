package com.example.peter.coupletones.tests;

import android.net.Uri;

import java.util.Arrays;

/**
 * ZZZN-119
 * Created by Anoxic on 060216.
 */
public class TestUserEditSoundAndVibetoneOfItsPartnersFavoriteLocations extends TestUserTemplate {

    private static final Uri SOUNDTONE_DUMMY_URI = Uri.parse("/path/to/file");
    private static final long[] VIBETONE_DUMMY_PATTERN = {110,110,110,110};

    public TestUserEditSoundAndVibetoneOfItsPartnersFavoriteLocations() {
        super();
    }

    /**
     * Scenario 2, 4
     * Since after writing the tasks we were told that arrival and departure tone can be the
     * same, so we combine those two scenarios into one test
     *
     * Instead of testing the pop-up window, simple set the properties and test the properties,
     * so some "when" and "then" statements have been slightly modified to satisfy this test
     */
    public void testUserCanEditSoundtoneOfAFavoriteLocation() {
        givenIHaveSomeFavoriteLocations();
        whenIEditSoundtoneOfAFacoriteLocation();
        thenTheSoundtoneOfThatLocationHasChanged();
    }

    /**
     * Scenario 1, 3
     * Since after writing the tasks we were told that arrival and departure tone can be the
     * same, so we combine those two scenarios into one test
     *
     * Instead of testing the pop-up window, simple set the properties and test the properties,
     * so some "when" and "then" statements have been slightly modified to satisfy this test
     */


    public void testUserCanEditVibetoneOfAFavoriteLocation() {
        givenIHaveSomeFavoriteLocations();
        whenIEditVibetoneOfAFacoriteLocation();
        thenTheVibetoneOfThatLocationHasChanged();
    }

    private void thenTheVibetoneOfThatLocationHasChanged() {
        assertTrue(Arrays.equals(VIBETONE_DUMMY_PATTERN, currentSelected.getVibetonePattern()));
    }

    private void whenIEditVibetoneOfAFacoriteLocation() {
        this.generateRandomNumber();
        currentSelected = myLocationManager.getLocationList().get(randomNumber);

        currentSelected.setVibetonePattern(VIBETONE_DUMMY_PATTERN);
    }

    private void thenTheSoundtoneOfThatLocationHasChanged() {
        assertTrue(SOUNDTONE_DUMMY_URI.equals(currentSelected.getSoundtoneUri()));
    }

    private void whenIEditSoundtoneOfAFacoriteLocation() {
        this.generateRandomNumber();
        currentSelected = myLocationManager.getLocationList().get(randomNumber);

        currentSelected.setSoundtoneUri(SOUNDTONE_DUMMY_URI);
    }

}
