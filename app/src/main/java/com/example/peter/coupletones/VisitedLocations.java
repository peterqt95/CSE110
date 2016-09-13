package com.example.peter.coupletones;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisitedLocations extends AppCompatActivity {
    ListView listView;
    String day;
    private SharedPreferences sharedPreferences = null;
    private static String LOCAL_STORAGE_KEY = "partnervisited";
    private static User user = Login.me;
    private static List<String> visitList = user.visitList;
    private static String[] values = user.values2;
    private static Set<String> set = user.set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        set = sharedPreferences.getStringSet("coolset", null);
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.partnerLocations);
        String visitString = sharedPreferences.getString(LOCAL_STORAGE_KEY, "");
        for(int i = 0; i < values.length; i++)
        {
            if(values[i] == null)
                values[i] = "";
        }

        set = sharedPreferences.getStringSet("coolset", null);
        if(set == null)
            set = new HashSet<String>();
        visitList = new ArrayList<>(set);
        set.addAll(visitList);
        editor.putStringSet("coolset",set);
        editor.apply();
        for(int j = 0; j < values.length; j++) {
            if(j > 0) {
                if (values[j - 1].equals(visitString))
                    break;
            }
            if(values[j].equals("") && !set.isEmpty()) {
                values[j] = visitList.get(j);
                break;
            }
            else if(values[j].equals("") && set.isEmpty())
            {
                Log.v("Index is:",""+j);
                values[j] = visitString;
                break;
            }
        }

        set.addAll(visitList);
        editor.putStringSet("coolset",set);
        editor.apply();

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // This is random, maybe it could be useful later
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(), "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startRingTonePicker(){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(intent, 1);
    }

}
