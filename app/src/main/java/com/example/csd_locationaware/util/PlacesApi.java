package com.example.csd_locationaware.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.csd_locationaware.R;
import com.example.csd_locationaware.controler.OnResponse;
import com.example.csd_locationaware.model.Bars;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class PlacesApi {
    private static String TAG = "@PLACESAPI";
    private static RequestQueue requestQueue;
    private  static String URL;
    private static OnResponse rsp;
    private static Context context;

    public PlacesApi(Context context, LatLng location, OnResponse response) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        updateLocation(location);
        rsp = response;
    }

    public static void getData() {
        Bars.refreshing = true;
        JsonObjectRequest rq = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    rsp.onResponse(response.getJSONArray("results"));
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: getData()", e);
                    Log.d(TAG, "onResponse: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: getData()", error);
            }
        });
        requestQueue.add(rq);
    }

    public static void updateLocation(LatLng location) {
        URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.latitude + ',' + location.longitude + "&rankby=distance&type=bar&key="+ context.getString(R.string.google_place_key);
    }
}
