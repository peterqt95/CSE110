package com.example.peter.coupletones;

import android.content.Context;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Vibrator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Runjie on 043016.
 * This is a class for location
 */
public class FirebaseLocation {
    private double lat;
    private double lng;
    private String name;
    private String userEmail;

    public FirebaseLocation() {
        if (Login.me != null)
            this.userEmail = Login.me.getMyEmail();
    }

    public FirebaseLocation(double lat, double lng)
    {
        this();
        this.lat = lat;
        this.lng = lng;
    }

    public FirebaseLocation(double lat, double lng, String name) {
        this();
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    @JsonProperty("longitude")
    public double getLng() {
        return lng;
    }

    @JsonProperty("latitude")
    public double getLat() {
        return lat;
    }

    @JsonProperty("locationName")
    public String getName() {
        return name;
    }

    @JsonProperty("userEmail")
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Lat: %.8f, Lng: %.8f", this.name, this.lat, this.lng);
    }

    /**
     * Overrides to make sure List.contains work
     *
     * @param o - the other MyLocation instance
     * @return true only if all the member fields are the same for both objects
     */
    @Override
    public boolean equals(Object o) {
        FirebaseLocation other = (FirebaseLocation) o;
        return this.lat == other.lat &&
                this.lng == other.lng &&
                this.name.equals(other.name);
    }

}
