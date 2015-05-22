package com.example.john.locationads;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AdLocationFragment extends Fragment {
    static LatLng AdLocation = null;
//    private GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2)).getMap();

    public AdLocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View AdlocationView = inflater.inflate(R.layout.ad_location, container, false);

//        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2)).getMap();
//        AdLocation = new LatLng(14.033, 80.166);
//        Marker AdLocationmarker = map.addMarker(new MarkerOptions().position(AdLocation).title("Current Location").snippet("This is your location"));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(AdLocation, 15));
//        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        return AdlocationView;
    }

    public void onDestroy(){

    }

    public void onDestroyView (){

    }

}