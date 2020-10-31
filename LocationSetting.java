package com.canada.cardelar.application;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
@SuppressWarnings("deprecation")
public class LocationSetting extends AppCompatActivity
        implements  OnMapReadyCallback
        , GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
        , LocationListener, GoogleMap.OnMarkerClickListener, ResultCallback {
EditText editTextLat,editTextLng;
CheckBox checkBoxNormalMap,checkBoxSatelliteMap;
Button buttonConform;
TextView textViewLocationDetails;
ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    com.google.android.gms.maps.model.LatLng latLngCurrentuserLocation=null;
LatLng companyLocation;
    Marker userMarker;
    Marker companyMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_location_setting );
        initView();
        SupportMapFragment mapFragment = ( SupportMapFragment )getSupportFragmentManager ( )// map fragment
                .findFragmentById ( R.id.map );
        mapFragment.getMapAsync ( this );



    }

    private void initView() {

        editTextLat=findViewById(R.id.editTextLat);
                editTextLng=findViewById(R.id.editTextLng);
        checkBoxNormalMap=findViewById(R.id.checkBoxNormalMap);
                checkBoxSatelliteMap=findViewById(R.id.checkBoxSatelliteMap);
       buttonConform=findViewById(R.id.buttonConformLocation);
        textViewLocationDetails=findViewById(R.id.locationDetails);
        checkBoxNormalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxNormalMap.isChecked()){
                    checkBoxSatelliteMap.setChecked(false);
                    if(GoogleMap.MAP_TYPE_NORMAL==mMap.getMapType()){

                    }else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        checkBoxNormalMap.setChecked(true);
                    }
                }else {
                    checkBoxNormalMap.setChecked(true);
                }
            }
        });


        checkBoxSatelliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxSatelliteMap.isChecked()){
                    checkBoxNormalMap.setChecked(false);
                    if(GoogleMap.MAP_TYPE_SATELLITE==mMap.getMapType()){

                    }else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        checkBoxSatelliteMap.setChecked(true);
                    }
                }else {
                    checkBoxSatelliteMap.setChecked(true);
                }
            }
        });

buttonConform.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        LatLng latLng=companyMarker.getPosition();
        showProgressDialog("updating Location");
        auth = FirebaseAuth.getInstance ( );
        firebaseUser = auth.getCurrentUser ( );
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child ("Dealer").child(firebaseUser.getUid());
          databaseReference.child("Lat").setValue(latLng.latitude);
          databaseReference.child("Lng").setValue(latLng.longitude);

       Intent intent= new Intent(LocationSetting.this,GoogleMapNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
});
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener ( this );//
        client = new GoogleApiClient.Builder ( this )
                .addApi ( LocationServices.API )
                .addOnConnectionFailedListener ( this )
                .addConnectionCallbacks ( this )
                .build ( );
        client.connect ( );
 showProgressDialog("As device discovers your location");

mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(getApplicationContext(),"Please drag marker to your company Location",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng latLng=marker.getPosition();
        editTextLat.setText(String.valueOf(latLng.latitude));
        editTextLng.setText(String.valueOf(latLng.longitude));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng=marker.getPosition();
        editTextLat.setText(String.valueOf(latLng.latitude));
        editTextLng.setText(String.valueOf(latLng.longitude));
    }
});
         mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
             @Override
             public void onMapClick(LatLng latLng) {
             companyMarker.remove();
                 drawMarker(latLng);
             }
         });
    }
    private void showProgressDialog(String message){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void drawMarker(LatLng latLng){
        editTextLat.setText(String.valueOf(latLng.latitude));
        editTextLng.setText(String.valueOf(latLng.longitude));
        MarkerOptions company = new MarkerOptions ( );
        company.title ( "Company Location" );
        company.position ( latLng);
        company.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_ORANGE) );
        companyMarker = mMap.addMarker ( company );
        companyMarker.setVisible ( true );
        companyMarker.showInfoWindow ();
        companyMarker.setDraggable(true);
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setVisible ( true );
        marker.showInfoWindow ( );
        return true;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    @Override
    public void onConnectionSuspended(int i) {}

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest ( ).create ( );//
        request.setPriority ( LocationRequest.PRIORITY_HIGH_ACCURACY );
        request.setInterval ( 5000 );

        if (ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder ( )
                .addLocationRequest ( request );
        builder.setAlwaysShow ( true );
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings (
                        client,
                        builder.build ( )
                );
        result.setResultCallback ( this );
        LocationServices.FusedLocationApi.requestLocationUpdates ( client, request, this );
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates ( client, this );
        if (location == null) {

            Toast.makeText ( getApplicationContext ( ), "Your location could not be found", Toast.LENGTH_SHORT ).show ( );
        } else {
            latLngCurrentuserLocation = new LatLng ( location.getLatitude ( ), location.getLongitude ( ) );
            companyLocation = new LatLng ( location.getLatitude ( )+0.002, location.getLongitude ( )+0.0032 );
            editTextLat.setText(String.valueOf(location.getLatitude()));
            editTextLng.setText(String.valueOf(location.getLongitude()));
            MarkerOptions company = new MarkerOptions ( );
            company.title ( "Company Location" );
            company.position ( companyLocation );
            company.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_ORANGE) );
            companyMarker = mMap.addMarker ( company );
            companyMarker.setDraggable(true);
            companyMarker.setVisible ( true );
            companyMarker.showInfoWindow ();

            MarkerOptions user = new MarkerOptions ( );
            user.title ( "Current Location" );
            user.position ( latLngCurrentuserLocation );
            user.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_GREEN ) );
            userMarker = mMap.addMarker (user );
            userMarker.setVisible ( true );
            userMarker.showInfoWindow ( );
            mMap.moveCamera ( CameraUpdateFactory.newLatLngZoom ( latLngCurrentuserLocation, 15 ) );
            progressDialog.dismiss();
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus ( );
        switch (status.getStatusCode ( )) {
            case LocationSettingsStatusCodes.SUCCESS:

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {

                    status.startResolutionForResult ( LocationSetting.this, 202 );

                } catch (IntentSender.SendIntentException e) {

                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }




}
