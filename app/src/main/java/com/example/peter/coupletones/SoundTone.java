package com.example.peter.coupletones;

import android.net.Uri;

/**
 * Created by Peter on 5/18/2016.
 */
public class SoundTone {

    private Uri current_ringtone;           // Uri to identify which ringtone
    private String current_ringtone_name;   // Name of the ringtone which is identified by the Uri

    public SoundTone(Uri ringtone, String name){
        current_ringtone = ringtone;
        current_ringtone_name = name;
    }

    // Get the ringtone uri
    public Uri getUri(){
        return current_ringtone;
    }

    // Set the new ringtone uri
    public void setUri(Uri uri){
        current_ringtone = uri;
    }

    // Get the name of the ringtone
    public String getRingtoneName(){
        return current_ringtone_name;
    }

    // Set the name of the ringtone
    public void setRingtoneName(String name){
        current_ringtone_name = name;
    }

}
