package com.example.csd_locationaware.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.csd_locationaware.R;
import com.example.csd_locationaware.controler.DoneLoading;
import com.example.csd_locationaware.controler.OnResponse;
import com.example.csd_locationaware.util.Bar;
import com.example.csd_locationaware.util.Bars;
import com.example.csd_locationaware.util.LocationUtil;
import com.example.csd_locationaware.util.PlacesApi;
import com.example.csd_locationaware.util.FetchURL;
import com.example.csd_locationaware.util.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        TaskLoadedCallback {
    String TAG = "@MAP";

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng currentLocation = null;
    private PlacesApi placesApi;
    private Polyline userPolyLine;

    private boolean StartUp = false;

    private MarkerOptions place1;
    private MarkerOptions place2;

    private Button directionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        placesApi = new PlacesApi(this, new LatLng(0, 0), new OnResponse() {
            @Override
            public void onResponse(JSONArray array) {
                Bars.generateBarList(array);
                Log.d(TAG, "onResponse: " + array.toString());
            }
        });
        Bars.setUp(new DoneLoading() {
            @Override
            public void doneLoading() {
                for (int i = 0; i < Bars.bars.size(); i++) {
                    Bar bar = Bars.bars.get(i);
                    mMap.addMarker(new MarkerOptions().position(bar.getLocation()).title(bar.getName()));

                }
                Log.i(TAG, "doneLoading: done loading bars...");
            }
        });

        directionsButton = findViewById(R.id.btn_showDirection);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            }
        });

        Toolbar toolbar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(toolbar);
        //Directions test

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment fragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mapView, fragment).commit();
        fragment.getMapAsync(this);

    }

    public void setUpDirectionsTest(){
        place1 = new MarkerOptions().position(new LatLng(51.571915, 4.768323)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");

        mMap.addMarker(place1);
        mMap.addMarker(place2);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     *
     *
     *
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        setUpDirectionsTest();
    }

    protected synchronized void buildGoogleApiClient() {
        boolean success = LocationUtil.checkLocationPermission(this);

        if (success) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            googleApiClient.connect();
        } else {
            finish();
        }
        Log.d(TAG, "buildGoogleApiClient: " + success);
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
        placesApi.updateLocation(currentLocation);

        if(!StartUp) {
            placesApi.getData();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(
                            location.getLatitude(),
                            location.getLongitude()
                    ), 15));
            StartUp = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationUtil.getNewLocationRequest();

        boolean success = LocationUtil.checkLocationPermission(this);
        Log.d(TAG, "onConnected: " + success);

        if (success) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_location) {
            Intent intent = new Intent(MapsActivity.this, Locations.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if(userPolyLine != null)
            userPolyLine.remove();
        userPolyLine = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
