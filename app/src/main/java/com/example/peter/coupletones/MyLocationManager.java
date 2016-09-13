package com.example.peter.coupletones;

import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.*;

/**
 * Created by Runjie on 043016.
 * Use this class to manage the location list
 */
public class MyLocationManager {
    /**
     * The key to be searched in sharedPreference
     */
    public String LOCAL_STORAGE_KEY = "locations";

    private SharedPreferences sharedPreferences = null;
    private MyLocation myNearestFavoriteLocationInRange = null;
    private MyLocation myCurrentLocation = null;
    private List<MyLocation> myFavoriteLocations = new ArrayList<>();
    private User user = Login.me;

    /**
     * For debugging only, draw a circle near any newly displayed locations
     */
    public void setStorageKey(String str) {
        LOCAL_STORAGE_KEY = str;
    }

    public boolean isShowingCircleAroundLocation = true;

    /**
     * Returns the favorite locations represented by JSONObject
     *
     * @return a JSONObject representing current favorite locations
     */
    private JSONObject getJSONFavoriteLocations() {
        JSONArray jsonLocationArray = new JSONArray();
        // Iterate over all the locations to create a JSON copy
        for (MyLocation myLocation : myFavoriteLocations) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(myLocation.NAME_NAME, myLocation.getName());
                jsonObject.put(myLocation.LAT_NAME, myLocation.getLat());
                jsonObject.put(myLocation.LNG_NAME, myLocation.getLng());
                jsonObject.put(myLocation.SOUNTONE_NAME, myLocation.getSoundtoneUri()
                        .toString());
                jsonObject.put(myLocation.VIBETONE_NAME, myLocation.getVibetonePatternString());
                jsonLocationArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version", "1.0");
            jsonObject.put(LOCAL_STORAGE_KEY, jsonLocationArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Updates the nearest favorite location.
     * To understand this method: just read it. The names are made to be readable
     */
    private void updateNearestFavoriteLocation() {
        if (myNearestFavoriteLocationInRange != null) {
            if (myNearestFavoriteLocationInRange.isInRangeOf(myCurrentLocation)) {
                if (myFavoriteLocations.isEmpty()) {
                    return;
                }
            } else {
                user.notifyPartnerDeparture(myNearestFavoriteLocationInRange);
                myNearestFavoriteLocationInRange = null;
            }
        }

        // Get the nearest favorite location
        MyLocation nearestFavoriteLocation = this.getNearestFavoriteLocationNear(myCurrentLocation);

        // Update the nearest favorite location if necessary
        if (nearestFavoriteLocation != null) {

            System.out.println("Nearest location is " + nearestFavoriteLocation);
            if (nearestFavoriteLocation.isInRangeOf(myCurrentLocation) && nearestFavoriteLocation
                    != myNearestFavoriteLocationInRange) {
                myNearestFavoriteLocationInRange = nearestFavoriteLocation;

                if (user == null) {
                    User tmpSupervisor = new User(user.getMyName(), user.getMyEmail());
                    tmpSupervisor.addPartnerEmail(user.getPartnerEmail());
                    tmpSupervisor.notifyPartnerArrival(nearestFavoriteLocation);
                } else {
                    user.notifyPartnerArrival(nearestFavoriteLocation);
                }
            }
        }

    }

    public MyLocationManager(User supervisor, SharedPreferences s) {
        this.user = supervisor;
        this.sharedPreferences = s;
    }

    /**
     * Adds a MyLocation to the user's favorite MyLocation
     *
     * @param myLocation - the MyLocation to be added
     */
    public void addLocation(MyLocation myLocation) {
        myFavoriteLocations.add(myLocation);
        // COMMENT FROM RUNJIE:
        /*
         Peter: for addLocation and removeLocation you both have `notifyLocation`, you may want to
         create two functions for each
         */
        user.addLocation(myLocation);
        this.saveLocationListToLocalStorage();
    }

    public void addPartnerLocation(MyLocation myLocation) {
        myFavoriteLocations.add(myLocation);
        // COMMENT FROM RUNJIE:
        /*
         Peter: for addLocation and removeLocation you both have `notifyLocation`, you may want to
         create two functions for each
         */
        this.saveLocationListToLocalStorage();
    }

    /**
     * Removes a MyLocation from the use's favorite locations
     *
     * @param myLocation - the location to be removed
     * @return true is removal is successful, false otherwise
     */
    public boolean removeLocation(MyLocation myLocation) {
        boolean isRemoved = myFavoriteLocations.remove(myLocation);
        if (isRemoved) {
            this.saveLocationListToLocalStorage();
        }
        user.removeLocation(myLocation);
        return isRemoved;
    }

    public boolean removePartnerLocation(MyLocation myLocation) {
        boolean isRemoved = myFavoriteLocations.remove(myLocation);
        if (isRemoved) {
            this.saveLocationListToLocalStorage();
        }
        return isRemoved;
    }

    /**
     * Updates current MyLocation for this user
     *
     * @param myLocation - the MyLocation to be updated
     */
    public void updateCurrentLocation(MyLocation myLocation) {
        this.myCurrentLocation = myLocation;

        this.updateNearestFavoriteLocation();
    }

    public void updateCurrentLocation(Location location) {
        MyLocation myLocation = new MyLocation(location.getLatitude(), location.getLongitude());
        this.updateCurrentLocation(myLocation);
    }

    public final List<MyLocation> getLocationList() {
        return this.myFavoriteLocations;
    }

    /**
     * Updates the data from the local storage, then update `myFavoriteLocations`
     */
    public void updateLocationListFromLocalStorage() {
        String jsonString = sharedPreferences.getString(LOCAL_STORAGE_KEY, "");

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            // Version of the object
            if (jsonObject.getString("version")
                    .equals("1.0")) {
                JSONArray jsonArray = jsonObject.getJSONArray(LOCAL_STORAGE_KEY);

                for (int i = 0; i != jsonArray.length(); ++i) {

                    JSONObject locationObject = (JSONObject) jsonArray.get(i);

                    MyLocation myLocation = new MyLocation(locationObject.getDouble(MyLocation
                            .LAT_NAME), locationObject.getDouble(MyLocation.LNG_NAME),
                            locationObject.getString(MyLocation.NAME_NAME));

                    // Soundtone
                    Uri uri = Uri.parse(locationObject.getString(MyLocation.SOUNTONE_NAME));
                    myLocation.setSoundtoneUri(uri);

                    // Vibetone, needs a little processing
                    String vibetoneString = locationObject.getString(MyLocation.VIBETONE_NAME);
                    String[] patternString = vibetoneString.split(MyLocation.VIBETONE_DELIM);
                    // Convert String[] to long[]
                    long[] newPattern = new long[patternString.length];
                    for (int j = 0; j != newPattern.length; ++j) {
                        try {
                            newPattern[j] = Long.parseLong(patternString[j]);
                        } catch (NumberFormatException e) {
                            // Do nothing - what should I do?
                        }
                    }
                    // Set the vibetone
                    myLocation.setVibetonePattern(newPattern);

                    if (!this.myFavoriteLocations.contains(myLocation)) {
                        myFavoriteLocations.add(myLocation);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the nearest favorite location near a specified location
     *
     * @param randomLocation - a random location the caller specified
     * @return a reference to the nearest favorite location, or null if the location list is empty
     */
    public MyLocation getNearestFavoriteLocationNear(MyLocation randomLocation) {
        MyLocation nearestFavoriteLocation = null;
        float nearestFavoriteLocationDistance = Float.MAX_VALUE;

        for (MyLocation myLocation : this.myFavoriteLocations) {
            if (myLocation.distanceBetween(randomLocation) < nearestFavoriteLocationDistance) {
                nearestFavoriteLocation = myLocation;
            }
            nearestFavoriteLocationDistance = nearestFavoriteLocation.distanceBetween
                    (randomLocation);
        }

        return nearestFavoriteLocation;
    }

    /**
     * Writes the data stored on this device to the local storage
     */
    public void saveLocationListToLocalStorage() {
        JSONObject jsonObject = this.getJSONFavoriteLocations();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCAL_STORAGE_KEY, jsonObject.toString());
        editor.apply();
    }

}
