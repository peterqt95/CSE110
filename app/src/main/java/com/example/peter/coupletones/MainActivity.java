package com.example.peter.coupletones;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private User currentUser = Login.me;
    private static SharedPreferences sharedPreferences = null;
    private static Settings settings;

    public DataSnapshot testSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        readNotificationSettings();

        // Add partner button to move to add partner screen
        Button addPartner = (Button) findViewById(R.id.addPartnerButton);
        addPartner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddPartner.class));

            }
        });

        // Add MyLocation button to move to add favorite location screen
        Button addLocation = (Button) findViewById(R.id.addLocationButton);
        addLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapManager.class));
                MapManager.init(currentUser, getSharedPreferences("data", MODE_PRIVATE));
            }
        });


        // View partner location button to move to view partner's visited locations
        Button partnerLocations = (Button) findViewById(R.id.viewPartnerLocations);
        partnerLocations.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PartnerLocations.class));
            }
        });

        Button visited = (Button) findViewById(R.id.visited);
        visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VisitedLocations.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        //PARTNER LINKING
        final Firebase partner = new Firebase("https://radiant-torch-6522.firebaseio.com");
        partner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                testSnapshot = snapshot;
                String myID = "";
                String partnerID = "";
                String messageContent = "";

                // Interpret and translate the data
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Message msg = postSnapshot.getValue(Message.class);
                    myID = msg.getReceiverID();
                    partnerID = msg.getSenderID();
                    messageContent = msg.getMessage();
                }

                // Test if this is the correct user to notify
                if (currentUser != null && (currentUser.getPartnerEmail().equals(partnerID)) &&
                        currentUser.getMyEmail().equals(myID)) {
                    toastMessage(messageContent);
                    partner.removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());
            }
        });

         //Reads messages and send a notification if necessary
        final Firebase ref = new Firebase("https://radiant-torch-6522.firebaseio.com");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                testSnapshot = snapshot;
                String myID = "";
                String partnerID = "";
                String messageContent = "";
                double lng = 0.0;
                double lat = 0.0;
                String locName = "";

                // Interpret and translate the data
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Message msg = postSnapshot.getValue(Message.class);
                    myID = msg.getReceiverID();
                    partnerID = msg.getSenderID();
                    messageContent = msg.getMessage();
                    lng = msg.getLng();
                    lat = msg.getLat();
                    locName = msg.getLocName();
                }

                // Test if this is the correct user to notify
                if (currentUser != null && (currentUser.getPartnerEmail()
                        .equals(partnerID)) && currentUser.getMyEmail()
                        .equals(myID)) {
                    Log.d("Emamil", "PARTNERS ARE LINKED");
                    // Yes it is, notify this user
                    // First, we need to extract my partner's location from the message
                    MyLocation notificationLocation = new MyLocation(lat, lng,
                            locName);
                    // Second, we need to determine the type of the message - whether my partner
                    // is leaving or arriving
                    if (notificationLocation != null) {
                        // These two locations basically help us determine the sound and
                        // vibetone to be played
                        MyLocation leavingLocation = null;
                        MyLocation arrivingLocation = null;

                        // Determine the location the partner just left (if any) and the
                        // location the partner just arrived (if any)
                        if (messageContent.startsWith(User.LEAVING_HEADER)) {
                            // My partner is leaving his previously visited location

                            // In this case, we need to modify the body of the notification by
                            // inserting my partner's last visited location
                            messageContent.replace(User.LEAVING_HEADER, User.LEAVING_HEADER +
                                currentUser.getMyPartnersLastVisitedLocation());

                            // Set leaving location
                            leavingLocation = notificationLocation;
                        } else if (messageContent.startsWith(User.ARRIVING_HEADER)) {
                            // My partner is arriving a new location
                            // In this case, we need to see if the partner has a previously
                            // visited location
                            MyLocation myPartnersLastVisitedLocation = currentUser
                                .getMyPartnersLastVisitedLocation();
                            if (myPartnersLastVisitedLocation != null) {
                                // It does, we need to see if we want to play both leaving
                                // and arriving sound
                                if (User.IS_PLAYING_LEAVING_AND_ARRIVING_SOUND_TOGETHER) {
                                    // Yes we do, add leaving location
                                    leavingLocation = myPartnersLastVisitedLocation;
                                }

                                // We are arriving a new location anyways
                                arrivingLocation = notificationLocation;
                            }
                        }


                        // Use to play sound or vibetone on arrive/departure
                        MyLocationManager myLocationManager = new MyLocationManager(Login.me,
                                getSharedPreferences("data", MODE_PRIVATE));
                        myLocationManager.setStorageKey("partnerLocations");
                        myLocationManager.updateLocationListFromLocalStorage();
                        List<MyLocation> partnerLocationList = myLocationManager.getLocationList();
                        for (int i = 0; i < partnerLocationList.size(); i++) {
                            MyLocation location = partnerLocationList.get(i);
                            String name = location.getName();
                            if(name.equals(locName)){
                                if(Settings.isPlaySound) {
                                    Uri uri = location.getSoundtoneUri();
                                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
                                    r.play();
                                }
                                if(Settings.isPlayVibetone){
                                    long[] pattern = location.getVibetonePattern();
                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(pattern, -1);
                                    Log.v("VIBETONE", "Vibetone is vibrating");
                                }
                            }
                        }

                    }
                    // Finally, post the toast message
                    toastMessage(messageContent);
                    if(!messageContent.contains(currentUser.getMyEmail()) && !messageContent.contains(currentUser.getMyName())) {
                        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("partnervisited", messageContent);
                        editor.apply();
                    }
                    ref.removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());
            }
        });

        //Reads locations
        final Firebase add = new Firebase("https://coupletonesstuff.firebaseio.com");
                add.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("RECEIVED", "FIREBASE LOCATIONS");
                testSnapshot = snapshot;
                FirebaseLocation msg = new FirebaseLocation();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    msg = postSnapshot.getValue(FirebaseLocation.class);
                }
                if (currentUser != null && currentUser.getPartnerEmail()
                        .equals(msg.getUserEmail())) {
                    MyLocation location = new MyLocation(msg.getLat(), msg.getLng(), msg.getName());
                    MyLocationManager myLocationManager = new MyLocationManager(currentUser,
                            getSharedPreferences("data", MODE_PRIVATE));
                    myLocationManager.setStorageKey("partnerLocations");
                    myLocationManager.updateLocationListFromLocalStorage();

                    myLocationManager.addPartnerLocation(location);
                    toastMessage("NEW LOCATION ADDED");
                    add.removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());
            }
        });

        //Reads locations
        final Firebase remove = new Firebase("https://removelocations.firebaseio.com");
        remove.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("REMOVED", "FIREBASE LOCATIONS");
                testSnapshot = snapshot;
                FirebaseLocation msg = new FirebaseLocation();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    msg = postSnapshot.getValue(FirebaseLocation.class);
                }
                if (currentUser != null && currentUser.getPartnerEmail()
                        .equals(msg.getUserEmail())) {
                    MyLocation location = new MyLocation(msg.getLat(), msg.getLng(), msg.getName());
                    MyLocationManager myLocationManager = new MyLocationManager(currentUser,
                            getSharedPreferences("data", MODE_PRIVATE));
                    myLocationManager.setStorageKey("partnerLocations");
                    myLocationManager.updateLocationListFromLocalStorage();
                    List<MyLocation> partnerLocationList = myLocationManager.getLocationList();

                    //If that location is already in the list, remove it, else add it
                    boolean found = false;
                    for (Iterator<MyLocation> iter = partnerLocationList.listIterator();
                         iter.hasNext(); ) {
                        MyLocation listLocations = iter.next();
                        if (listLocations.equals(location)) {
                            myLocationManager.removePartnerLocation(listLocations);
                            found = true;
                            toastMessage("LOCATION REMOVED");
                            remove.removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());
            }
        });
    }

    public void toastMessage(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Reads the notification settings from the shared preference, and update the toggle accordingly
     */
    private void readNotificationSettings() {
        // Read the data
        settings.setSharedPreference(getSharedPreferences(settings.FILENAME, MODE_PRIVATE));
        settings.readSharedPreference();

        // Get the switches
        Switch soundToggle = (Switch) findViewById(R.id.sound_toggle);
        Switch vibetoneToggle = (Switch) findViewById(R.id.vibe_toggle);

        // Toggle notification switch if it's on
        if (settings.isPlaySound) {
            soundToggle.setChecked(true);
        }
        if (settings.isPlayVibetone) {
            vibetoneToggle.setChecked(true);
        }

        // Add listeners
        soundToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.turnOnSound();
                } else {
                    settings.turnOffSound();
                }
            }
        });

        vibetoneToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.turnOnVibetone();
                } else {
                    settings.turnOffVibetone();
                }
            }
        });
    }

}
