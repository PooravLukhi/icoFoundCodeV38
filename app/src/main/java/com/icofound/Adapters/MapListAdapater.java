package com.icofound.Adapters;
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icofound.Activity.MapActivity;
import com.icofound.R;


public class MapListAdapater extends RecyclerView.Adapter<MapListAdapater.MapViewHolder>{

    Activity activity;
    String[] mLikelyPlaceNames;
    LatLng[] mLikelyPlaceLatLngs;
    String[] mLikelyPlaceAttributions;
    String[] mLikelyPlaceAddresses;
    GoogleMap mMap;
    ProgressDialog mprogressdialog;

    public MapListAdapater(Activity activity, String[] mLikelyPlaceNames, String[] mLikelyPlaceAddresses, LatLng[] mLikelyPlaceLatLngs, String[] mLikelyPlaceAttributions, GoogleMap mMap, ProgressDialog mprogressdialog, LatLng mylocation) {

        this.activity = activity;
        this.mLikelyPlaceAddresses = mLikelyPlaceAddresses;
        this.mLikelyPlaceAttributions = mLikelyPlaceAttributions;
        this.mLikelyPlaceLatLngs = mLikelyPlaceLatLngs;
        this.mLikelyPlaceNames = mLikelyPlaceNames;
        this.mMap = mMap;
        this.mprogressdialog = mprogressdialog;
    }

    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.map_list,parent,false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position == 0){
            mprogressdialog.show();
        }
        holder.text1.setText(mLikelyPlaceNames[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mMap.clear();
                LatLng markerLatLng = mLikelyPlaceLatLngs[position];
                String markerSnippet = mLikelyPlaceAddresses[position];
                if (mLikelyPlaceAttributions[position] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[position];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                if (markerLatLng!=null){
                    mMap.addMarker(new MarkerOptions()
                            .title(mLikelyPlaceNames[position])
                            .position(markerLatLng)
                            .snippet(markerSnippet));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
                    Intent intent = new Intent(activity, MapActivity.class);
                    intent.putExtra("latitude",mLikelyPlaceLatLngs[position].latitude);
                    intent.putExtra("longtitude",mLikelyPlaceLatLngs[position].longitude);
                    activity.setResult(RESULT_OK,intent);
                    activity.finish();
                }else{

                }

                // Position the map's camera at the location of the marker.

            }
        });

        if (position == mLikelyPlaceNames.length -1){
            mprogressdialog.dismiss();
        }

    }

    @Override
    public int getItemCount() {
        return mLikelyPlaceNames.length;
    }

    public class MapViewHolder extends RecyclerView.ViewHolder {

        TextView text1;
        public MapViewHolder(@NonNull View itemView) {
            super(itemView);

            text1 = itemView.findViewById(R.id.text1);
        }
    }
}
