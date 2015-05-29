package com.example.john.locationads;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

public class Ad_Fragment extends Fragment {
    private Marker marker;
    private TextView location;
    private TextView name;
    private TextView snippet;

    public Ad_Fragment(Marker ad_marker) {
         marker = ad_marker;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        getActivity().setContentView(R.layout.ad_fragment);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ad_fragment, container, false);

        location = (TextView) view.findViewById(R.id.location);
        snippet = (TextView) view.findViewById(R.id.snippet);
        name = (TextView) view.findViewById(R.id.name);
        Log.d("name:",marker.getTitle());
        name.setText(marker.getTitle());
        location.setText(marker.getPosition().latitude +" "+ marker.getPosition().longitude);
        snippet.setText(marker.getSnippet());

        return view;
    }

}
