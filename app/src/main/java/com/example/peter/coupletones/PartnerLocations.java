package com.example.peter.coupletones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import java.util.List;

public class PartnerLocations extends AppCompatActivity {
    private static SharedPreferences sharedPreferences;
    ListView listView;
    List<MyLocation> partnerLocationList;
    int currPosition;
    String currSoundName;
    String currVibeName;
    MyLocationManager myLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.partnerLocations);

        // Defined Array values to show in ListView
        myLocationManager = new MyLocationManager(Login.me,
                getSharedPreferences("data", MODE_PRIVATE));
        myLocationManager.setStorageKey("partnerLocations");
        myLocationManager.updateLocationListFromLocalStorage();
        partnerLocationList = myLocationManager.getLocationList();
        String[] values = new String[partnerLocationList.size()];
        for (int i = 0; i < partnerLocationList.size(); i++) {
            MyLocation location = partnerLocationList.get(i);
            String name = location.getName();
            values[i] = name;
        }

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // This is random, maybe it could be useful later
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Play the sound so we know its there
                Uri notification = partnerLocationList.get(position)
                        .getSoundtoneUri();
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();

                // Make sure that we can vibrate
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(partnerLocationList.get(position).getVibetonePattern(), -1);
                if(v.hasVibrator()){
                    Log.v("vibrator","meow");
                }

                // Keep track of the current SoundTone/VibeTone properties
                currPosition = position;
                currSoundName = r.getTitle(getApplicationContext());
                currVibeName = partnerLocationList.get(position)
                        .getVibeTone()
                        .getName();

                CharSequence options[] = new CharSequence[]{"Edit SoundTone", "Edit VibeTone"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PartnerLocations.this);
                builder.setTitle("Edit a Tone:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            startRingTonePicker();
                        } else if (which == 1) {
                            startVibeTonePicker();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void startVibeTonePicker() {
        CharSequence vibrations[] = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10"};
        AlertDialog.Builder vibeBuilder = new AlertDialog.Builder(PartnerLocations.this);
        vibeBuilder.setTitle("Current vibration: " + currVibeName);
        vibeBuilder.setItems(vibrations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Generate a vibration pattern sequence
                int option = which + 1;
                long vibration = option * 100;
                long sleep = option * 150;
                long[] newPattern = new long[option * 2];
                int i = 0;
                while (i < option * 2) {
                    newPattern[i] = vibration;
                    i++;
                    newPattern[i] = sleep;
                    i++;
                }

                // Update the VibeTone properties
                partnerLocationList.get(currPosition)
                        .getVibeTone()
                        .setPattern(newPattern);
                partnerLocationList.get(currPosition)
                        .getVibeTone()
                        .setName(Integer.toString(option));
                Log.v("startVibeTonePicker", "set tone");
                myLocationManager.saveLocationListToLocalStorage();
            }
        });
        vibeBuilder.show();
    }

    private void startRingTonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Current Tone: " + currSoundName);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent
            intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            // Get the location we are selected at and set the new tone selected
            String name = RingtoneManager.getRingtone(this, uri)
                    .getTitle(this);
            partnerLocationList.get(currPosition)
                    .getSoundTone()
                    .setUri(uri);
            partnerLocationList.get(currPosition)
                    .getSoundTone()
                    .setRingtoneName(name);

            myLocationManager.saveLocationListToLocalStorage();
        }
    }


}
