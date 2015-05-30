package com.example.john.locationads;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerParcelable implements Parcelable {

    private LatLng location;
    private String name;
    private String snippet;
    private double lat,lng;

    public MarkerParcelable(Marker marker){
        this.location = marker.getPosition();
        this.name = marker.getTitle();
        this.snippet = marker.getSnippet();
        this.lat = location.latitude;
        this.lng = location.longitude;
    }

    // Parcelling part
    public MarkerParcelable(Parcel cm){
        this.lat = cm.readDouble();
        this.lng = cm.readDouble();
        this.name = cm.readString();
        this.snippet = cm.readString();
    }

    public double getLat(){
        return this.lat;
    }

    public double getLng(){
        return this.lng;
    }

    public String getname(){
        return this.name;
    }

    public String getSnippet(){
        return this.snippet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(name);
        dest.writeString(snippet);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MarkerParcelable createFromParcel(Parcel cm) {
            return new MarkerParcelable(cm);
        }

        public MarkerParcelable[] newArray(int size) {
            return new MarkerParcelable[size];
        }
    };
}
