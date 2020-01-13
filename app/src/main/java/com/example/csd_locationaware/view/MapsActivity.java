package com.example.csd_locationaware.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.csd_locationaware.R;
import com.example.csd_locationaware.controler.DoneLoading;
import com.example.csd_locationaware.controler.OnResponse;
import com.example.csd_locationaware.model.Bar;
import com.example.csd_locationaware.model.Bars;
import com.example.csd_locationaware.util.LocationUtil;
import com.example.csd_locationaware.util.PlacesApi;
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

import org.json.JSONArray;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    String TAG = "@MAP";

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng currentLocation = null;
    private PlacesApi placesApi;
    private DoneLoading doneLoading;

    private boolean StartUp = false;

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

        Toolbar toolbar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment fragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mapView, fragment).commit();
        fragment.getMapAsync(this);

        doneLoading = new DoneLoading() {
            @Override
            public void doneLoading() {
                drawMarkers();
                Log.i(TAG, "doneLoading: done loading bars...");
            }
        };
        Bars.setUp(doneLoading);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
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

    public void drawMarkers() {
        for (int i = 0; i < Bars.bars.size(); i++) {
            Bar bar = Bars.bars.get(i);
            mMap.addMarker(new MarkerOptions().position(bar.getLocation()).title(bar.getName()));
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        placesApi.updateLocation(currentLocation);

        if (!StartUp) {
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
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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

    @Override
    protected void onResume() {
        drawMarkers();
        super.onResume();
    }
}
