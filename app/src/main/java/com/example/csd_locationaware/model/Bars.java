package com.example.csd_locationaware.model;

import android.util.Log;

import com.example.csd_locationaware.controler.DoneLoading;
import com.example.csd_locationaware.model.Bar;
import com.example.csd_locationaware.view.Locations;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Bars {
    private static String TAG = "@BARS";
    public static List<Bar> bars = new ArrayList<>();
    private static boolean setUp = false;
    private static DoneLoading done;
    public static boolean refreshing = false;

    public static void setUp(DoneLoading doneLoading) {
        setUp = true;
        done = doneLoading;
    }

    public static void generateBarList(JSONArray array) {
        if (setUp) {
            bars.clear();
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jBar = array.getJSONObject(i);
                    String name = jBar.getString("name");
                    LatLng location = new LatLng(jBar.getJSONObject("geometry").getJSONObject("location").getDouble("lat"), jBar.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    String address = jBar.getString("vicinity");

                    Bar bar = new Bar(name, location, address);
                    bars.add(bar);

                    Log.d(TAG, "generateBarList: " + bar.toString());
                } catch (Exception e) {

                    Log.e(TAG, "generateBarList: ", e);
                }
            }
        } else Log.i(TAG, "generateBarList: Please call setUp() before trying to generate bars!");
        refreshing = false;
        try {
            Locations.doneRefreshing();
        } catch (NullPointerException e) {//catch for first bootup, its not very pretty but it works for now
        }
        done.doneLoading();
    }
}
