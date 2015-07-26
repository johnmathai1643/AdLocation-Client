package com.example.john.locationads;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapterMarker implements GoogleMap.InfoWindowAdapter {

    private Context mContext;
    private Bitmap bitmap;

    public InfoWindowAdapterMarker(Context context,Bitmap bitmap) {
        this.mContext = context;
        this.bitmap = bitmap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        // Getting view from the layout file info_window_layout
        View view = inflater.inflate(R.layout.info_window_layout, null);

        TextView popUpTitle = (TextView) view.findViewById(R.id.title);
        TextView popUpContent = (TextView) view.findViewById(R.id.snippet);
        ImageView popUpImage = (ImageView) view.findViewById(R.id.bitmap);

        popUpTitle.setText(marker.getTitle());
        popUpContent.setText(marker.getSnippet());
        popUpImage.setImageBitmap(this.bitmap);

        // Load the image thumbnail
//        final String imagePath = markers.get(marker.getId());
//        ImageLoader imageLoader = ((AppConfig)mContext.getApplicationContext()).getImageLoader();
//        imageLoader.loadBitmap(imagePath, popUpImage, 0, 0, onImageLoaded);

        return view;
    }

}

