package com.example.peter.coupletones;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapManager extends FragmentActivity implements OnMapReadyCallback {

    private static final int UPDATE_INTERVAL_MILLISECONDS = 0;
    private static final float LONG_CLICK_DETECTION_SENSITIVITY = .3f;

    private static GoogleMap mMap;
    private static LocationManager locationManager;
    private static SharedPreferences sharedPreferences;

    private static MyLocationManager myLocationManager;
    private static User supervisor;

    /**
     * Updates the location list from local storage and then update the display on the map
     */
    private static void updateLocationListFromLocalStorage() {
        myLocationManager.updateLocationListFromLocalStorage();

        if (mMap != null) {
            mMap.clear();
        }

        List<MyLocation> list = myLocationManager.getLocationList();
        for (MyLocation myLocation : list) {
            displayLocation(myLocation);
        }
    }

    /**
     * Displays this MyLocation on the map
     *
     * @param myLocation - the MyLocation to be added on the map
     */
    private static void displayLocation(MyLocation myLocation) {
        LatLng latLng = new LatLng(myLocation.getLat(), myLocation.getLng());
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(myLocation.getName()));

            if (myLocationManager.isShowingCircleAroundLocation) {
                mMap.addCircle(new CircleOptions().center(latLng)
                        .radius(myLocation.RADIUS_IN_MILE * myLocation.METER_PER_MILE));
            }
        }
    }

    /**
     * Enables tracking the user location on a regular basis
     */
    private void enableTrackingUserLocation() {
        // LocationManager to enable GPS
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocationManager.updateCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = locationManager.GPS_PROVIDER;

        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION}, 100);
            return;
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }

        locationManager.requestLocationUpdates(locationProvider, UPDATE_INTERVAL_MILLISECONDS, 0,
                locationListener);

        // Todo make this work
        // Go to my location
        Location myPosition = locationManager.getLastKnownLocation(locationProvider);
        if (myPosition != null) {
            double latitude = myPosition.getLatitude();
            double longitude = myPosition.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 5);
            mMap.animateCamera(yourLocation);
        }
    }

    private void enableAddingFavoriteLocation() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                // Test if this location is near any location already in the list
                final MyLocation myLocation = new MyLocation(latLng.latitude, latLng.longitude);

                List<MyLocation> locations = myLocationManager.getLocationList();
                for (MyLocation location : locations) {
                    if (location.isInRangeOf(myLocation)) {
                        CharSequence text = "Too close to another existing location";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(MapManager.this, text, duration);
                        toast.show();

                        return;
                    }
                }

                // Setup the window to ask for a name
                AlertDialog.Builder builder = new AlertDialog.Builder(MapManager.this);
                builder.setTitle("Add this location");

                // Set up the input
                final EditText input = new EditText(MapManager.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newTitle = input.getText()
                                .toString();
                        myLocation.setName(newTitle);
                        MyLocation myLocation = new MyLocation(latLng.latitude, latLng.longitude,
                                newTitle);
                        Log.d("BUG@", String.valueOf(myLocation));
                        myLocationManager.addLocation(myLocation);
                        displayLocation(myLocation);
                        System.out.println("Added a location: " + myLocation);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void enableRemovingFavoriteLocation() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                final MyLocation whereClicked = new MyLocation(latLng.latitude, latLng.longitude);

                // Get the nearest location of where the user just clicked
                final MyLocation nearestFavoriteLocation = myLocationManager
                        .getNearestFavoriteLocationNear(whereClicked);

                if (nearestFavoriteLocation.distanceBetween(whereClicked) <
                        LONG_CLICK_DETECTION_SENSITIVITY) {

                    // Setup the window to ask for removal
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapManager.this);
                    builder.setTitle("Remove the location \"" + nearestFavoriteLocation.getName()
                            + "\"?");

                    // Set up the buttons
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (myLocationManager.removeLocation(nearestFavoriteLocation)) {

                                updateLocationListFromLocalStorage();
                                CharSequence text = "Location removed";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(MapManager.this, text, duration);
                                toast.show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                    return;
                }
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Zoom bar and current location
        mMap.getUiSettings()
                .setMyLocationButtonEnabled(true);
        mMap.getUiSettings()
                .setZoomControlsEnabled(true);

        // Make the map clickable
        enableAddingFavoriteLocation();
        enableRemovingFavoriteLocation();
        enableTrackingUserLocation();

        // Load the data
        updateLocationListFromLocalStorage();
    }

    /**
     * Sets the supervisor of this map manager to read the data
     *
     * @param user - the user of this map
     */
    public static void setSupervisor(User user) {
        supervisor = user;
    }

    public static void setSharedPreferences(SharedPreferences s) {
        sharedPreferences = s;
    }

    public static void init(User currentUser, SharedPreferences data) {
        setSupervisor(currentUser);
        setSharedPreferences(data);

        myLocationManager = new MyLocationManager(currentUser, sharedPreferences);
    }

}
