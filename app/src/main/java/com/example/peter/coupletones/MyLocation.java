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
public class MyLocation {
    public final static float RADIUS_IN_MILE = .1f;
    public final static float METER_PER_MILE = 1609.34f;
    public final static String LAT_NAME = "lat";
    public final static String LNG_NAME = "lng";
    public final static String NAME_NAME = "name";
    public final static String SOUNTONE_NAME = "soundtone";
    public final static String VIBETONE_NAME = "vibetone";
    public final static String VIBETONE_DELIM = ",";

    private double lat;
    private double lng;
    private String name;
    private SoundTone soundTone;
    private VibeTone vibeTone;


    public MyLocation() {
        soundTone = new SoundTone(RingtoneManager.getDefaultUri(RingtoneManager
                .TYPE_NOTIFICATION), "Default");
        vibeTone = new VibeTone();
    }

    public MyLocation(double lat, double lng) {
        soundTone = new SoundTone(RingtoneManager.getDefaultUri(RingtoneManager
                .TYPE_NOTIFICATION), "Default");
        vibeTone = new VibeTone();
        this.lat = lat;
        this.lng = lng;
    }

    public MyLocation(double lat, double lng, String name) {
        soundTone = new SoundTone(RingtoneManager.getDefaultUri(RingtoneManager
                .TYPE_NOTIFICATION), "Default");
        vibeTone = new VibeTone();
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SoundTone getSoundTone() {
        return soundTone;
    }

    public VibeTone getVibeTone() {
        return vibeTone;
    }


    /**
     * Returns the uri of this location's ringtone
     *
     * @return - the uri of this location's ringtone
     */
    //@JsonProperty("arrivingSound")
    public Uri getSoundtoneUri() {
        return soundTone.getUri();
    }

    /**
     * Sets the uri of this location's ringtone
     */
    public void setSoundtoneUri(Uri uri) {
        this.soundTone.setUri(uri);
    }

    /**
     * Returns the pattern of this location's vibetone
     * <p/>
     * Visit
     * https://developer.android.com/reference/android/os/Vibrator.html#vibrate(long[], int)
     * to see more information
     *
     * @return - an array that represents the vibetone pattern
     */
    //@JsonProperty("leavingVibration")
    public long[] getVibetonePattern() {
        return vibeTone.getPattern();
    }

    /**
     * Sets the uri of this location's ringtone
     */
    public void setVibetonePattern(long[] newPattern) {
        this.vibeTone.setPattern(newPattern);
    }

    /**
     * Returns the pattern of this location's vibetone in string, separated by comma
     *
     * @return - a string that can represent the vibetone pattern
     */
    public String getVibetonePatternString() {
        long[] pattern = vibeTone.getPattern();

        // Join them by DELIM
        String str = "";
        String delim = "";
        for (long p : pattern) {
            str.concat(delim)
                    .concat(Long.toString(p));
            delim = VIBETONE_DELIM;
        }

        return str;
    }

    /**
     * Returns the distance between this location and another one, measured in mile
     *
     * @param location - the other MyLocation
     */
    public float distanceBetween(MyLocation location) {
        float[] result = new float[5];

        Location.distanceBetween(this.lat, this.lng, location.lat, location.lng, result);

        return 0.000621371f * result[0];
    }

    /**
     * Returns true if this MyLocation is within the radius of another MyLocation
     *
     * @param myLocation - the other MyLocation
     * @return true if this MyLocation is within the radius of another MyLocation
     */
    public boolean isInRangeOf(MyLocation myLocation) {
        return this.distanceBetween(myLocation) < RADIUS_IN_MILE;
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
        MyLocation other = (MyLocation) o;
        return this.lat == other.lat &&
                this.lng == other.lng &&
                this.name.equals(other.name);
    }

}
