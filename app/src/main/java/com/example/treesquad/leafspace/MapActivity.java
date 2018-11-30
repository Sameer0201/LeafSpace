package com.example.treesquad.leafspace;

import com.example.treesquad.leafspace.db.TreeRecord;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    //constants
    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    //private static final LatLng mDefaultLocation = new LatLng(0f,0f);

    //vars
    private boolean mLocPermGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFused;
    private Location mLastKnownLocation;
    private Marker currMarker;
    private ArrayList<Marker> mMarkers;
    private HashMap<String,GeoPoint> mTrees;
    private ImageView mInfo;
    private FirebaseFirestore db;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mInfo = findViewById(R.id.marker_info);
        mMarkers = new ArrayList<Marker>();
        mTrees = new HashMap<String,GeoPoint>();

        db = FirebaseFirestore.getInstance();
        getTrees();

        getLocPerm();

        mFused = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initMap() {
        SupportMapFragment mapFragment  = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"Map is ready!", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        if(mLocPermGranted) {
            updateLocationUI();
            getDeviceLocation();
        }

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try{
                    if(currMarker.isInfoWindowShown()){
                        currMarker.hideInfoWindow();
                    }else{
                        currMarker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );
                }
            }
        });
    }

    private void getLocPerm() {
        Log.d(TAG,"Getting location permissions");
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocPermGranted = true;
            initMap();
        }
        else {
            ActivityCompat.requestPermissions(this,perms,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult: called");
        mLocPermGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocPermGranted = true;
                    Log.d(TAG,"onRequestPermissionsResult: success");
                    initMap();
                }
                else {
                    mLocPermGranted = false;
                    Log.d(TAG,"onRequestPermissionsResult: failure");
                    return;
                }
            }
        }
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocPermGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocPerm();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocPermGranted) {
                Task locationResult = mFused.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            Log.d(TAG,"Found location");
                            mLastKnownLocation = (Location)task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            Toast.makeText(MapActivity.this,"Couldn't obtain device location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void dropMarker(LatLng pos, String title, String snippet) {
        MarkerOptions options = new MarkerOptions()
                .position(pos)
                .title(title)
                .snippet(snippet);
        Marker newMarker = mMap.addMarker(options);
        mMarkers.add(newMarker);
        Log.d(TAG,"Added marker with title: " + title);
    }

    public boolean onMarkerClick(Marker marker) {
        currMarker = marker;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currMarker.getPosition()));
        return true;
    }

    public void getTrees() {
        db.collection("trees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                GeoPoint loc = (GeoPoint)document.getData().get("location");
                                LatLng spot = new LatLng(loc.getLatitude(),loc.getLongitude());
                                mTrees.put(id,loc);
                                dropMarker(spot,"Cool tree","Cool info");

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
