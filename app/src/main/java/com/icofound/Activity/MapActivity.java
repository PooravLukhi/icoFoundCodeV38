package com.icofound.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.icofound.AESUtils;
import com.icofound.Adapters.MapListAdapater;
import com.icofound.Class.Constant;
import com.icofound.Class.Utils;
import com.icofound.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    RecyclerView lstPlaces;
    private PlacesClient mPlacesClient, msearchPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final int M_MAX_ENTRIES = 3;
    private String[] mLikelyPlaceNames, mLikelyPlaceAddresses, mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private GoogleMap mMap, currentmMap;
    double latitude, longtitude;
    private LatLng mylocation;
    private List<String> placeIds = new ArrayList<>();
    ProgressDialog mprogressdialog;
    LinearLayout share_loc;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        utils = new Utils(MapActivity.this);
        utils.intialise();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mprogressdialog = new ProgressDialog(this);
        mprogressdialog.setCancelable(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLocationPermission();

        lstPlaces = findViewById(R.id.listPlaces);
        share_loc = findViewById(R.id.share_loc);

//        try {
//            Places.initialize(getApplicationContext(), AESUtils.decrypt("3E9704A5D699D6E07933ED26F92C113EC8C7CB889D4F786F72CF0DC6CE8F7604D89B8847BABF353F07EC9BE05DD20FEE"));
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }

        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        share_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MessageActivity.class);
                intent.putExtra("latitude", mylocation.latitude);
                intent.putExtra("longtitude", mylocation.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        currentmMap = googleMap;
        pickCurrentPlace();
        getLocationPermission();
    }

    private void getCurrentPlaceLikelihoods() {
        mprogressdialog.show();
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        final FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(this,
                new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful()) {
                            FindCurrentPlaceResponse response = task.getResult();
                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (response.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                                count = response.getPlaceLikelihoods().size();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

//                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (int i1 = 0; i1 < count; i1++) {
                                PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(i1);
                                Place currPlace = placeLikelihood.getPlace();
                                mLikelyPlaceNames[i1] = currPlace.getName();
                                mLikelyPlaceAddresses[i1] = currPlace.getAddress();
                                mLikelyPlaceAttributions[i1] = (currPlace.getAttributions() == null) ?
                                        null : TextUtils.join(" ", currPlace.getAttributions());
                                mLikelyPlaceLatLngs[i1] = currPlace.getLatLng();
                                if (i1 == (count - 1)) {
                                    fillPlacesList();
                                }
                            }
//                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                                Place currPlace = placeLikelihood.getPlace();
//                                mLikelyPlaceNames[i] = currPlace.getName();
//                                mLikelyPlaceAddresses[i] = currPlace.getAddress();
//                                mLikelyPlaceAttributions[i] = (currPlace.getAttributions() == null) ?
//                                        null : TextUtils.join(" ", currPlace.getAttributions());
//                                mLikelyPlaceLatLngs[i] = currPlace.getLatLng();
//
////                                String currLatLng = (mLikelyPlaceLatLngs[i] == null) ?
////                                        "" : mLikelyPlaceLatLngs[i].toString();
//
////                                Log.i(TAG, String.format("Place " + currPlace.getName()
////                                        + " has likelihood: " + placeLikelihood.getLikelihood()
////                                        + " at " + currLatLng));
//
//                                i++;
//                                if (i == (count - 1)) {
//                                    fillPlacesList();
//                                }
//                            }

                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                            }
                            mprogressdialog.dismiss();
                        }
                    }
                });
    }

    private void getSearchPlaceLikelihoods( Intent data, Place place) {
        mprogressdialog.show();
        placeIds.clear();
        mMap.clear();
        msearchPlacesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude),
                new LatLng(place.getLatLng().latitude, place.getLatLng().longitude));
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setOrigin(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(place.getName())
                .build();

        msearchPlacesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            if (response != null) {
                List<AutocompletePrediction> prediction1 = response.getAutocompletePredictions();
                int count;
                if (prediction1.size() < M_MAX_ENTRIES) {
                    count = prediction1.size();
                } else {
                    count = M_MAX_ENTRIES;
                }
                mLikelyPlaceNames = new String[count];
                mLikelyPlaceAddresses = new String[count];
                mLikelyPlaceAttributions = new String[count];
                mLikelyPlaceLatLngs = new LatLng[count];

                for (int i = 0; i < count; i++) {
                    final String placeId = prediction1.get(i).getPlaceId();
                    placeIds.add(placeId);
                    if (i == (count - 1)) {
                        getplace(place);
                    }
                }
            } else {
                mprogressdialog.dismiss();
            }

        }).addOnFailureListener((exception) -> {
            mprogressdialog.dismiss();
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

    }

    private void getplace(Place place) {
        for (int i = 0; i < this.placeIds.size(); i++) {
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            final FetchPlaceRequest request1 = FetchPlaceRequest.newInstance(this.placeIds.get(i), placeFields);
            int finalI = i;
            msearchPlacesClient.fetchPlace(request1).addOnSuccessListener((response1) -> {
                if (response1 != null) {
                    Place currPlace = response1.getPlace();
                    if (currPlace != null) {
                        mLikelyPlaceNames[finalI] = currPlace.getName();
                        mLikelyPlaceAddresses[finalI] = currPlace.getAddress();
                        mLikelyPlaceAttributions[finalI] = (currPlace.getAttributions() == null) ?
                                null : TextUtils.join(" ", currPlace.getAttributions());
                        mLikelyPlaceLatLngs[finalI] = currPlace.getLatLng();

                        if (finalI == (this.placeIds.size() - 1)) {
                            searchfillPlacesList(place.getName(), place.getAddress(), place.getLatLng(), place.getAttributions());
                        }
                    }

                }
            });
        }

    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                latitude = mLastKnownLocation.getLatitude();
                                longtitude = mLastKnownLocation.getLongitude();
                                mylocation = new LatLng(latitude, longtitude);


                                Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                                Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                                currentmMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                currentmMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).title("Your current Location").icon((bitmapDescriptorFromVector(MapActivity.this, R.drawable.ic_my_location))));
                            } else {

                                latitude = mDefaultLocation.latitude;
                                longtitude = mDefaultLocation.longitude;
                                mylocation = new LatLng(latitude, longtitude);
                                currentmMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(latitude, longtitude), DEFAULT_ZOOM));
                                currentmMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).title(Constant.location));

                            }
                            currentmMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longtitude)));


                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            currentmMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        }

                        getCurrentPlaceLikelihoods();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void pickCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            getDeviceLocation();
        } else {
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));
            getLocationPermission();
        }
    }

    private void fillPlacesList() {

        MapListAdapater adapater = new MapListAdapater(MapActivity.this, mLikelyPlaceNames, mLikelyPlaceAddresses, mLikelyPlaceLatLngs, mLikelyPlaceAttributions, mMap, mprogressdialog, this.mylocation);
        lstPlaces.setAdapter(adapater);
        mprogressdialog.dismiss();
    }

    private void searchfillPlacesList(String name, String address, LatLng latLng, List<String> attributions) {
        LatLng markerLatLng = latLng;
        String markerSnippet = address;
        if (attributions != null) {
            markerSnippet = markerSnippet + "\n" + attributions;
        }

        mMap.addMarker(new MarkerOptions()
                .title(name)
                .position(markerLatLng)
                .snippet(markerSnippet)
                .icon((BitmapDescriptorFactory.fromResource(R.drawable.pin))));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        currentmMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).title("Your current Location").icon((bitmapDescriptorFromVector(MapActivity.this, R.drawable.ic_my_location))));

        MapListAdapater adapater = new MapListAdapater(MapActivity.this, mLikelyPlaceNames, mLikelyPlaceAddresses, mLikelyPlaceLatLngs, mLikelyPlaceAttributions, mMap, mprogressdialog, this.mylocation);
        lstPlaces.setAdapter(adapater);
        mprogressdialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_geolocate:
                pickCurrentPlace();
                return true;

            case R.id.action_search:

                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapActivity.this);
                startActivityForResult(intent, 100);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void getLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = null;
            if (data != null) {
                place = Autocomplete.getPlaceFromIntent(data);
            }

            if (place != null) {
                getSearchPlaceLikelihoods(data, place);
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
        }
    }

}