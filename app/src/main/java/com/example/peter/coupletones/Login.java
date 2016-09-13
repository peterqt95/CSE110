package com.example.peter.coupletones;


import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * A login screen that offers login via email/password.
 */

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    /**
     * Static variables for sign in procedure
     */
    private static final String TAG = "SignInActivity";
    private static final int SELECT_ACCOUNT = 1;
    private static final int SIGN_IN = 2;

    // Partner creation
    public static User me;
    public SharedPreferences sharedPreferences;
    // Google sign in
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Google Sign in */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mGoogleApiClient.isConnected()) {
                signOut();
            }
            else {
                mGoogleApiClient.connect();
                if(mGoogleApiClient.isConnecting()){
                    toastMessage("Attempting to connect...");
                }
                else{
                    toastMessage("Connection failure. Check network settings");
                }
            }
            }
        });

    }

    /* Start sign in activity */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN);
    }

    /* Sign out of account so that users can reselect a desired account */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        signIn();
                    }
                });
    }

    /* Get result of sign in */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            me = new User(acct.getDisplayName(), acct.getEmail());

            // Start new activity and save email
            toastMessage("Welcome " + acct.getDisplayName());
            sharedPreferences = getSharedPreferences("partneremail",MODE_PRIVATE);
            String email = "";
            email = sharedPreferences.getString("email","");
            me.addPartnerEmail(email);
            if (me.getPartnerEmail().equals("")) {
                toastMessage("NO PARTNER, PLEASE ADD ONE");
                startActivity(new Intent(Login.this, AddPartner.class));
            }
            else {
                startActivity(new Intent(Login.this, MainActivity.class));
                toastMessage(me.getPartnerEmail());
            }
        } else {
            // Sign in fail, toast to notify failure
            toastMessage("Failure to login, please try again");
            Log.d(TAG, "Failure due to: " + result.getStatus().getStatusCode());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /* Toast messages */
    public void toastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


}
