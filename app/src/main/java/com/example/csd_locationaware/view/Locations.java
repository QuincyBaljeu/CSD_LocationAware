package com.example.csd_locationaware.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.csd_locationaware.R;
import com.example.csd_locationaware.controler.DoneLoading;
import com.example.csd_locationaware.controler.LocationsAdapter;
import com.example.csd_locationaware.util.PlacesApi;

public class Locations extends AppCompatActivity {

    private static SwipeRefreshLayout refreshLayout;
    private static LocationsAdapter adapter;
    private static String TAG = "@LOCATIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        Toolbar toolbar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshLayout = findViewById(R.id.swipeContainer);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_locations);
        adapter = new LocationsAdapter(this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PlacesApi.getData();
            }
        });
    }

    public static void doneRefreshing() {
        refreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        Log.i(TAG, "doneRefreshing: finished refreshing...");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
