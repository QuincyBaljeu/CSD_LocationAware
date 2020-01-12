package com.example.csd_locationaware.util;

import com.google.android.gms.maps.model.LatLng;

public class Bar {
    private String name;
    private LatLng location;
    private String adress;

    public Bar(String name, LatLng location, String adress) {
        this.name = name;
        this.location = location;
        this.adress = adress;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getAdress() {
        return adress;
    }


    @Override
    public String toString() {
        return "Bar{" +
                "name='" + name + '\'' +
                ", location=" + location +
                ", adress='" + adress + '\'' +
                '}';
    }
}
