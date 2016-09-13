package com.example.peter.coupletones;

import android.util.Log;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Peter on 4/25/2016.
 */
public class User {
    /* Two Strings to identify if this message is for arriving or leaving */
    public static final String ARRIVING_HEADER = "[ARRIVING] ";
    public static final String LEAVING_HEADER = "[LEAVING] ";
    public static List<String> visitList = new ArrayList<>(10);
    public  static String[] values2 = new String[10];
    public static Set<String> set = new HashSet<String>();

    /**
     * Whether arriving location "B" by leaving location "A" should also play both sound of
     * leaving "A" and entering "B"
     * <p>
     * If set to true, both sounds (for leaving and arriving) will be played; otherwise, only the
     * sound for arriving will be played
     */
    public static final boolean IS_PLAYING_LEAVING_AND_ARRIVING_SOUND_TOGETHER = true;

    private MyLocation myPartnersLastVisitedLocation = null;

    private String myName = "";
    private String myEmail = "";
    private String partnerEmail = "";

    public User() {
    }

    public User(String name, String email) {
        myName = name;
        myEmail = email;
    }

    public String getMyEmail() {
        return myEmail;
    }

    public String getMyName() {
        return myName;
    }

    public void addMyEmail(String email) {
        myEmail = email;
    }


    public MyLocation getMyPartnersLastVisitedLocation() {
        return myPartnersLastVisitedLocation;
    }

    public void setMyPartnersLastVisitedLocation(MyLocation myPartnersLastVisitedLocation) {
        this.myPartnersLastVisitedLocation = myPartnersLastVisitedLocation;
    }

    // public String getUserId() { return userID; }
    public void addPartnerEmail(String email) {
        partnerEmail = email;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }


    /**
     * Notifies the user's partner about its favorite MyLocation
     * This function will be called inside `updateNearestFavoriteLocation`
     * <p>
     * Structure of Storage in Firebase:
     * - message
     * |-> partnerID aka receiverID
     * |-> userID aka senderID
     * |-> messageContent
     * <p>
     * The partner will use the beginning of `messageContent` to tell if it's arriving or leaving
     *
     * @param myLocation - the MyLocation to notify the partner
     *                   Leave it as a null object to notify your partner that you have left
     *                   somewhere
     */
    public void notifyPartnerArrival(MyLocation myLocation) {

        String timeStamp = new SimpleDateFormat("MM/dd/yyyy_HH:mm:ss").format(Calendar.getInstance()
                .getTime());

        FirebaseLocation loc = new FirebaseLocation(myLocation.getLat(), myLocation.getLng(),
                myLocation.getName());

        // Choose the correct representation of the location
        String favLocation = myLocation.getName();
        String msg = favLocation + " @ " + timeStamp;


        msg = ARRIVING_HEADER + msg;


        Firebase firebase = new Firebase("https://radiant-torch-6522.firebaseio.com");
        Firebase message = firebase.child("message");

        Message toSend = new Message(partnerEmail, myEmail, msg, loc.getLng(), loc.getLat(), loc
                .getName());
        message.setValue(toSend);
    }

    public void notifyPartnerDeparture(MyLocation myLocation) {
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy_HH:mm:ss").format(Calendar.getInstance()
                .getTime());

        FirebaseLocation loc = new FirebaseLocation(myLocation.getLat(), myLocation.getLng(),
                myLocation.getName());

        // Choose the correct representation of the location
        String favLocation = loc.getName();
        String msg = favLocation + " @ " + timeStamp;

        msg = LEAVING_HEADER + msg;

        Firebase firebase = new Firebase("https://radiant-torch-6522.firebaseio.com");
        Firebase message = firebase.child("message");

        Message toSend = new Message(partnerEmail, myEmail, msg, loc.getLng(), loc.getLat(), loc
                .getName());
        message.setValue(toSend);
    }

    //USED TO SHOW THAT TWO PEOPLE ARE LINKED
    public void notifyPartner() {
        Log.d("NOTIFICATION", "notify partner called");
        Firebase firebase = new Firebase("https://radiant-torch-6522.firebaseio.com");
        Firebase message = firebase.child("partner");
        Message toSend = new Message(partnerEmail, myEmail, partnerEmail + " and " + myEmail + " " +
                "LINKED", 0.0, 0.0, "");
        message.setValue(toSend);
    }

    //USE TO UPDATE FAVORITE LOCATION LISTS
    public void removeLocation(MyLocation myLocation) {
        Log.d("LOCATION", "SENDING TO PARTNER");
        Firebase firebase = new Firebase("https://removelocations.firebaseio.com");
        Firebase use = firebase.child("remove");
        FirebaseLocation loc = new FirebaseLocation(myLocation.getLat(), myLocation.getLng(),
                myLocation.getName());
        use.setValue(loc);
    }

    //USE TO UPDATE FAVORITE LOCATION LISTS
    public void addLocation(MyLocation myLocation) {
        Log.d("LOCATION", "SENDING TO PARTNER");
        Firebase firebase = new Firebase("https://coupletonesstuff.firebaseio.com");
        Firebase use = firebase.child("add");
        FirebaseLocation loc = new FirebaseLocation(myLocation.getLat(), myLocation.getLng(),
                 myLocation.getName());
        use.setValue(loc);
    }

    /**
     * Returns a `MyLocation` object given a string from the notification method
     * @param messageContent - the notification method
     * @return the location extracted from the message string
     */
    /*public MyLocation extractPartnersLocationFromNotificationMessge(String messageContent) {
        String firstLetter = String.valueOf(messageContent.charAt(1));
        //USE CHARACTER #11 for the loc name
        //ARRIVING

    }*/
}
