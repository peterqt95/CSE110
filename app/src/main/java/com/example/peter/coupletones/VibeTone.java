package com.example.peter.coupletones;

import android.os.Vibrator;

/**
 * Created by Peter on 5/25/2016.
 */
public class VibeTone {
    private String name;                                 // name of our unique vibration
    private long[] vibrationPattern = {100, 150};        // default vibration pattern

    /*
     * Create a default VibeTone
     */
    public VibeTone(){
        this.name = "1";
    }

    /*
     * Create a VibeTone by giving it a name and a unique vibration pattern
     */
    public VibeTone(String name, long[] vibration){
        this.name = name;
        this.vibrationPattern = vibration;
    }

    /*
     * Returns the vibration pattern of this VibeTone
     */
    public long[] getPattern(){
        return this.vibrationPattern;
    }

    /*
     * Sets the new vibration pattern of this VibeTone
     */
    public void setPattern(long[] newPattern){
        this.vibrationPattern = newPattern;
    }

    /*
     * Returns the name of the VibeTone
     */
    public String getName(){
        return this.name;
    }

    /*
     * Sets the name of the VibeTone
     */
    public void setName(String name){
        this.name = name;
    }
}
