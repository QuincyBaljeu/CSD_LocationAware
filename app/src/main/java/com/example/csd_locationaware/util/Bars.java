package com.example.csd_locationaware.util;

import android.util.Log;

import com.example.csd_locationaware.controler.DoneLoading;
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
        }else Log.i(TAG, "generateBarList: Please call setUp() before trying to generate bars!");

        done.doneLoading();
    }

    public static String getDirectionsUrl(LatLng origin, LatLng destination){
        String url = getUrl(new LatLng(origin.latitude, origin.longitude), new LatLng(destination.latitude, destination.longitude), "walking");
        return url;
    }

    private static String getUrl(LatLng origin, LatLng dest, String directionMode) {
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
        //TODO API key from resource value
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBifZx4KQ7SVgsSKdwW6H8mO-XuepF2Ur";
        return url;
    }
}
