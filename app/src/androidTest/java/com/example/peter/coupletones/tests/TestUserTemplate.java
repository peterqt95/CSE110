package com.example.peter.coupletones.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

import com.example.peter.coupletones.MainActivity;
import com.example.peter.coupletones.MapManager;
import com.example.peter.coupletones.MyLocation;
import com.example.peter.coupletones.MyLocationManager;
import com.example.peter.coupletones.User;

/**
 * This test can be used as a template to test stories
 * Created by Runjie on 050516.
 */
abstract public class TestUserTemplate<T> extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;
    MapManager mapManager;
    MyLocationManager myLocationManager;
    SharedPreferences sharedPreferences;

    MyLocation[] locationArray = {new MyLocation(30, 120, "Location1"), new MyLocation(30.0001,
            120, "Location2"), new MyLocation(30, 120.0001, "Location3"), new MyLocation(30.0001,
            120.0001, "Location4"), new MyLocation(30.0002, 120, "Location5")};
    MyLocation currentSelected;
    int randomNumber;

    public TestUserTemplate() {
        super(MainActivity.class);
    }

    /**
     * Generate a random number, and update `randomNumber`
     */
    protected void generateRandomNumber() {
        randomNumber = (int) (Math.random() * locationArray.length);
    }

    protected void givenICanSeeTheMap() {
        // Basic setup
        mainActivity = (MainActivity) getActivity();
        sharedPreferences = mainActivity.getSharedPreferences("data", Context.MODE_PRIVATE);

        User user = new User("dummy", "dummy");
        mapManager = new MapManager();
        MapManager.init(user, sharedPreferences);

        myLocationManager = new MyLocationManager(user, sharedPreferences);
    }

    protected void givenIHaveSomeFavoriteLocations() {
        givenICanSeeTheMap();

        // Create dummy location data
        for (MyLocation myLocation : locationArray) {
            myLocationManager.addLocation(myLocation);
        }

        // Save it
        myLocationManager.saveLocationListToLocalStorage();
    }

}
