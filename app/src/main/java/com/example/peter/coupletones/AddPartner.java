package com.example.peter.coupletones;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class AddPartner extends AppCompatActivity {
    User user = Login.me;
    GoogleCloudMessaging gcm;
    String PROJECT_NUMBER = "661499447815";
    String regid;
    private static SharedPreferences sharedPreferences;
    EditText partnerRegId;
    EditText userRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add's partner regID
        Button sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                partnerRegId = (EditText) findViewById(R.id.partnerID);
                user.addPartnerEmail(partnerRegId.getText().toString());
                Toast.makeText(AddPartner.this, "Added Partner ID: " + user.getPartnerEmail(), Toast.LENGTH_SHORT).show();
                user.notifyPartner();

                String email = "";
                sharedPreferences = getSharedPreferences("partneremail",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                email = partnerRegId.getText().toString();
                editor.putString("email",email);
                editor.apply();

                //SHARED PREFERENCE ADD PARTNER
                startActivity(new Intent(AddPartner.this, MainActivity.class));
            }
        });

        // Retrieve our own device ID

        userRegId = (EditText) findViewById(R.id.userRegId);
        userRegId.setText(user.getMyEmail());
    }

    /*
     * Gets the user's android ID for GCM, maybe needed for MS2
     */
    /*public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            String msg;
            @Override
            protected String doInBackground(Void... params ){
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    regid = gcm.register(PROJECT_NUMBER);
                    user.addId(regid);
                    msg = "Device registered: " + regid;
                    Log.i("GCM: ", regid);
                }
                catch(IOException ex){
                    msg = "Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg){
                userRegId = (EditText) findViewById(R.id.userRegId);
                userRegId.setText(regid);
            }
        }.execute(null, null, null);
    }*/

}
