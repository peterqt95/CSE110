package com.example.peter.coupletones;

import android.content.SharedPreferences;

/**
 * Created by Runjie on 052216.
 * This is a class to manage the settings
 */
public class Settings {
    private static SharedPreferences sharedPreference;
    private static final String SOUND_KEY = "sound";
    private static final String VIBETONE_KEY = "vibetone";


    /**
     * Saves the shared preference file
     * This method is to be called everytime a setting is changed to save it
     */
    private static void saveSharedPreference() {
        if (sharedPreference != null) {
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putBoolean(SOUND_KEY, isPlaySound);
            editor.putBoolean(VIBETONE_KEY, isPlayVibetone);
            editor.apply();
        }

    }

    public static boolean isPlaySound;
    public static boolean isPlayVibetone;

    public static final String FILENAME = "settings";

    /**
     * Toggles the play sound
     *
     * @return the new status of playSound
     */
    public static boolean togglePlaySound() {
        if (isPlaySound) {
            turnOffSound();
        } else {
            turnOnSound();
        }

        return isPlaySound;
    }

    /**
     * Toggles vibetone
     *
     * @return the new status of vibetone
     */
    public static boolean togglePlayVibetone() {
        if (isPlayVibetone) {
            turnOffVibetone();
        } else {
            turnOnVibetone();
        }

        return isPlayVibetone;
    }

    public static void turnOnSound() {
        isPlaySound = true;
        saveSharedPreference();

    }

    public static void turnOffSound() {
        isPlaySound = false;
        saveSharedPreference();
    }

    public static void turnOnVibetone() {
        isPlayVibetone = true;
        saveSharedPreference();
    }

    public static void turnOffVibetone() {
        isPlayVibetone = false;
        saveSharedPreference();
    }

    public static void setSharedPreference(SharedPreferences s) {
        sharedPreference = s;
    }

    public static void readSharedPreference() {
        if (sharedPreference != null) {
            isPlaySound = sharedPreference.getBoolean(SOUND_KEY, false);
            isPlayVibetone = sharedPreference.getBoolean(VIBETONE_KEY, false);
        }
    }
}
