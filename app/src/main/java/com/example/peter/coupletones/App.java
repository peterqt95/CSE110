package com.example.peter.coupletones;

import android.app.Application;
import com.firebase.client.Firebase;

/**
 * Created by Samuel on 5/6/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
