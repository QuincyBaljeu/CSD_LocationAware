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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class PlacesApi {
    private String TAG = "@PLACESAPI";
    private RequestQueue requestQueue;
    private String URL;
    private OnResponse rsp;
    private Context context;

    public PlacesApi(Context context, LatLng location, OnResponse response) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        updateLocation(location);
        this.rsp = response;
    }

    public void getData() {
        JsonObjectRequest rq = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //rsp.onResponse(response.getJSONArray("candidates"));
                    Log.d(TAG, "onResponse: " + response.getJSONArray("results"));
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
        this.requestQueue.add(rq);
    }

    public void updateLocation(LatLng location) {
        this.URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.latitude + ',' + location.longitude + "&radius=1500&type=bar&key="+ context.getString(R.string.google_place_key);
    }

}
