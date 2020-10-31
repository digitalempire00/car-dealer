package com.canada.cardelar.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canada.cardelar.application.DataBase.SQLiteDataBase;

import com.canada.cardelar.application.Models.AdapterData;
import com.canada.cardelar.application.Models.BitmapUtility;
import com.canada.cardelar.application.Models.DealerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class GoogleMapNavigation extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener
, LocationListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks
,ResultCallback
{
    LatLng testLatLng =new LatLng(45.407164, -75.703719);
    String profileType = "non";
    private GoogleMap mMap;
    NavigationView navigationView;
    TextView textName, textEmail;
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleApiClient client;
    ProgressDialog progressDialog;
    DatabaseReference referenceDealer, referenceUsers;
    DatabaseReference databaseReference;
    LocationRequest locationRequest;
    LatLng currentLocation;
    Marker userMarker;
    SQLiteDataBase sqLiteDataBase=new SQLiteDataBase(this);
    ImageView profileImage;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    BitmapUtility bitmapUtility=new BitmapUtility();

    LinearLayout mapLinearLayout,listLinearLayout,filterLinearLayout;
    View mapView,listView,filterView;
RecyclerView recyclerView;
     List<AdapterData> dealerList;
     DealerAdapter dealerAdapter;

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_navigation);

        if (!CheckGooglePlayServices()) {
           Toast.makeText(getApplicationContext(),
                   "Finishing test case since Google Play Services are not available",
            Toast.LENGTH_LONG).show();
            finish();
        }

        else {
            Toast.makeText(getApplicationContext(),
                    "Google Play Services available.",
                    Toast.LENGTH_LONG).show();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textName = (TextView) header.findViewById(R.id.title_text);
        textEmail = (TextView) header.findViewById(R.id.email_text);
        profileImage=header.findViewById(R.id.user_profile_image);
        mapLinearLayout=findViewById(R.id.mapLauout);
        listLinearLayout=findViewById(R.id.listLayout);
        filterLinearLayout=findViewById(R.id.filterLayout);
        mapView=findViewById(R.id.mapView);
        listView=findViewById(R.id.listView);
        filterView=findViewById(R.id.filterView);

        mapLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
               mapView.setBackgroundColor(getColor(R.color.view_color));
                listView.setBackgroundColor(getColor(R.color.wedgitColor));
                filterView.setBackgroundColor(getColor(R.color.wedgitColor));
            }
        });
        listLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                mapView.setBackgroundColor(getColor(R.color.wedgitColor));
                listView.setBackgroundColor(getColor(R.color.view_color));
                filterView.setBackgroundColor(getColor(R.color.wedgitColor));

            }
        });
        filterLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Under Construction");

            }
        });


        ImageViewRounded(BitmapFactory.decodeResource(getResources(), R.drawable.user_place_holder));
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        referenceDealer = FirebaseDatabase.getInstance().getReference().child("Dealer");
        referenceUsers = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference = FirebaseDatabase.getInstance().
                getReference().child("places_details").child("canada");

    }

    private void intiRecycLerView() {
        if(currentLocation==null){
            showToast("please wait application want your Current Location");
            return;
        }
        Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_place);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        dealerAdapter = new DealerAdapter(dealerList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(dealerAdapter);
        dealerAdapter.setOnItemClickListener(new DealerAdapter.onItemClickListener(){  //clicking on edit Response button
            @Override
            public void editClick(int position) {
               Intent intent=new Intent(GoogleMapNavigation.this,PlaceDetailsActivity.class);
                intent.putExtra("placeId",dealerList.get(position).getPlaceId());
                startActivity(intent);

            }
        });
        for (int i=0;i<dealerList.size();i++){
            if(dealerList.get(i).getImageURL().equals("")){

                dealerList.get(i).setDealerImage(bitmapUtility.setSideRounded(placeholder,placeholder.getWidth(),placeholder.getHeight()));
                dealerAdapter.notifyDataSetChanged();
            }else{
                downloadImage(dealerList.get(i).getImageURL(),i);

            }

        }


    }
    private void downloadImage(String Url, final int i) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        StorageReference imageReference = storageReference.child(Url);
        long MAXBYTS = 1024 * 1024;
        imageReference.getBytes(MAXBYTS).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap mbitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                dealerList.get(i).setDealerImage(bitmapUtility.setSideRounded(mbitmap,mbitmap.getWidth(),mbitmap.getHeight()));
                dealerAdapter.notifyDataSetChanged();
            }
        });
    }

















    private void PushDataOnMap() {
        dealerList = new ArrayList<>();
        Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.simple_placeholder);
        int counter=0;
        mMap.clear();
        drawCurrentLocationMarker();
        if(sqLiteDataBase.countPlaces()>0) {
            if (true) {
                Double radius =1000.00;
               Double nearest = 6371.0;
                LatLng nearestLatLng = null;
                LatLng latLngPlacePosition;
                Cursor cursor = sqLiteDataBase.getAllPlaces();

                if (cursor.moveToNext()) {
                    do {

                        latLngPlacePosition = new LatLng(cursor.getDouble(9), cursor.getDouble(11));
                       Double distance = RouteLength(currentLocation,latLngPlacePosition );
                       if(counter<30){

                       dealerList.add(new AdapterData(cursor.getString(12),String.valueOf(distance),cursor.getString(18),
                               cursor.getString(22),bitmapUtility.setSideRounded(placeholder,placeholder.getWidth(),placeholder.getHeight()),cursor.getString(15),cursor.getString(16)));
                       counter++;
                       }

                        if (distance <= radius) {
                         if (distance < nearest) {
                                nearest = distance;
                                nearestLatLng = new LatLng(cursor.getDouble(9), cursor.getDouble(11));
                            }
                            drawMarker(latLngPlacePosition,
                                    cursor.getString(12),
                                    cursor.getString(18),
                                    cursor.getString(16));
                        }


                    } while (cursor.moveToNext());
                    if (nearestLatLng != null) {
                        drawRoute(currentLocation,nearestLatLng, mMap);
                    }

                }

      intiRecycLerView();
            } else {
                showToast("Please Select Distance In KM");

            }
        }else {
            showToast("Application getting data ");

        }
    }

    private void drawCurrentLocationMarker() {
        MarkerOptions user = new MarkerOptions ( );
        user.title ( "Current Location" );
        user.position ( currentLocation );
        user.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_RED) );
        userMarker = mMap.addMarker (user );
        userMarker.setVisible ( true );
        userMarker.showInfoWindow ( );
        userMarker.setTag("userMarker");
        mMap.moveCamera ( CameraUpdateFactory.newLatLngZoom ( currentLocation, 10 ) );
    }

    private void showToast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void getUserInfo() {
        //showProgressDialog("Application getting User data");
        referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = auth.getCurrentUser();
                if (!dataSnapshot.child(user.getUid()).child("Lat").exists()) {
                    profileType = "User";
                    String userName = dataSnapshot.child(user.getUid()).child("Name").getValue(String.class);
                    String userEmail = dataSnapshot.child(user.getUid()).child("Email").getValue(String.class);
                    String imageURl;
                    if (dataSnapshot.child(user.getUid()).child("userProfileImage").exists()) {
                        imageURl = dataSnapshot.child(user.getUid()).child("userProfileImage").getValue(String.class);
                        downloadProfileImage(imageURl);
                    }
                    textName.setText(userName);
                    textEmail.setText(userEmail);

                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.user_options);
                   // progressDialog.dismiss();

                } else {
                    profileType = "Dealer";
                    referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseUser user1 = auth.getCurrentUser();


                            String user_name = dataSnapshot.child(user1.getUid()).child("Name").getValue(String.class);
                            String user_email = dataSnapshot.child(user1.getUid()).child("Email").getValue(String.class);
                            textName.setText(user_name);
                            textEmail.setText(user_email);

                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.dealer_options);

                           // progressDialog.dismiss();

                          PushDataOnMap();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "databaseError.getMessage()", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void downloadProfileImage(String imageURl) {


            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            StorageReference imageReference = storageReference.child(imageURl);
            long MAXBYTS = 1024 * 1024;
            imageReference.getBytes(MAXBYTS).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap mbitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageViewRounded(mbitmap);
                }
            });




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        client = new GoogleApiClient.Builder ( this )
                .addApi ( LocationServices.API )
                .addOnConnectionFailedListener ( this )
                .addConnectionCallbacks ( this )
                .build ( );
        client.connect ( );




        showProgressDialog("please wait device getting your locayion");
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (profileType.equals("Dealer")) {
            if (id == R.id.item_logout) {
                signOut();
            } else {
                ShowToast("Please this part is Under Constructions");
            }


        } else if (profileType.equals("User")) {
            if (id == R.id.logout) {
                signOut();
            } else {
                ShowToast("Please this part is Under Constructions");
            }


        }


        return false;


    }

    private void signOut() {
        if (auth != null) {
            auth.signOut();
            finish();
            startActivity(new Intent(GoogleMapNavigation.this, MainActivity.class));
        } else {
            ShowToast("please Try Again");

        }

    }

    private void ShowToast(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void  drawMarker(LatLng latLng,String Title,String snippet,String PlaceId){

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(Title);
                markerOptions.snippet(snippet);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                Marker marker=      mMap.addMarker(markerOptions);
                 marker.setTag(PlaceId);
        }


    //referense https://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2
    private double RouteLength(LatLng start, LatLng end) {
        Double lat1=start.latitude;
        Double lon1=start.longitude;
        Double lat2=end.latitude;
        Double lon2=end.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist= dist / 0.62137;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(marker.getTag().toString().equals("userMarker")){
            marker.setVisible ( true );
            marker.showInfoWindow ( );
            return true;
        }






      final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.map_selection_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView PlaceName,placeAddress,placePlusCode,placePhNumber,placeStatus,placeRoute,placeDistance;
        LinearLayout linearLayoutCancel,linearLayoutDirection,linearLayoutMoreDetails;
        //ratting
        TextView placeRatting,totalRatting;
        ImageView[] star=new  ImageView[5];
        PlaceName=dialog.findViewById(R.id.textViewPlaceName);
        placeAddress=dialog.findViewById(R.id.textViewPlaceAddress);
        placePlusCode=dialog.findViewById(R.id.textViewPlacePlusCode);
        placePhNumber=dialog.findViewById(R.id.textViewPlacePhoneNumber);
        placeStatus=dialog.findViewById(R.id.textViewPlaceStatus);
        placeRoute=dialog.findViewById(R.id.PlaceRoute);
        placeDistance=dialog.findViewById(R.id.distance);
        linearLayoutCancel=dialog.findViewById(R.id.linearLayoutCancel);
        linearLayoutDirection=dialog.findViewById(R.id.LinearLayoutDirection);
        linearLayoutMoreDetails=dialog.findViewById(R.id.LinearLayoutDetails);
        star[0]=dialog.findViewById(R.id.imageViewStar1);
        star[1]=dialog.findViewById(R.id.imageViewStar2);
        star[2]=dialog.findViewById(R.id.imageViewStar3);
        star[3]=dialog.findViewById(R.id.imageViewStar4);
        star[4]=dialog.findViewById(R.id.imageViewStar5);
        placeRatting=dialog.findViewById(R.id.textViewRatting);
        totalRatting=dialog.findViewById(R.id.textViewTotal);

       Cursor cursor=sqLiteDataBase.getSinglePlace(marker.getTag().toString());
       cursor.moveToFirst();
       if(cursor.getString(18).equals("")){
           placeRatting.setText("Ratting : ");
           totalRatting.setText("NA");
       }else {
           placeRatting.setText(cursor.getString(18));
           totalRatting.setText("(" + cursor.getString(22) + ")");
           Double dRate = cursor.getDouble(18);
           int Irate = dRate.intValue();
           double point = dRate - Irate;

           int totalStar=5;
           int i=0;
         for(;i<Irate;i++){
             star[i].setImageResource(R.drawable.ic_full_star);
             totalStar--;
         }
           if(totalStar!=0){
               if(point>=0.3){
                   star[i].setImageResource(R.drawable.ic_half_star);
                   i++;
                   totalStar--;
               }
           }

         for(;i<5;i++){
             star[i].setImageResource(R.drawable.ic_empty_star);
         }

       }





       totalRatting.setText("("+cursor.getString(22)+")");
     PlaceName.setText(cursor.getString(12));
     placeAddress.setText("Address : "+cursor.getString(6));
     placePlusCode.setText("vicinity : "+cursor.getString(23));
     placePhNumber.setText(cursor.getString(8));
     placeStatus.setText(cursor.getString(3));
     placeRoute.setText(cursor.getString(19));


        double distance = RouteLength ( currentLocation,marker.getPosition() );
        DecimalFormat df = new DecimalFormat ( "#.##" );
        String dist = df.format ( distance );
        placeDistance.setText(dist+" KM Away");
        dialog.show();
linearLayoutCancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        marker.setVisible ( true );
        marker.showInfoWindow ( );
        dialog.dismiss();
    }
});
        linearLayoutDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                marker.setVisible ( true );
                marker.showInfoWindow ( );
                mMap.moveCamera ( CameraUpdateFactory.newLatLngZoom ( marker.getPosition(), 12 ));
                drawRoute(currentLocation,marker.getPosition(),mMap);

            }
        });

        linearLayoutMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent;
                intent=new Intent(GoogleMapNavigation.this,PlaceDetailsActivity.class);
                intent.putExtra("placeId",marker.getTag().toString());
                startActivity(intent);
            }
        });

        return true;
    }


    public boolean drawRoute(LatLng origin, LatLng destination, GoogleMap mMap) {
        StringBuilder sb;
        Object[] dataTransfer = new Object[4];

        sb = new StringBuilder ( );
        sb.append ( "https://maps.googleapis.com/maps/api/directions/json?" );
        sb.append ( "origin=" + origin.latitude + "," + origin.longitude );
        sb.append ( "&destination=" + destination.latitude + "," + destination.longitude );
        sb.append ( "&key=" + "AIzaSyAaA9L4KHQty4lT89HEUOzFTXrGCGvdkKw" );
        DirectionAsync getDirectionsData = new DirectionAsync( getApplicationContext ( ) );
        dataTransfer[0] = mMap;
        dataTransfer[1] = sb.toString ( );
        dataTransfer[2] = new LatLng ( origin.latitude, origin.longitude );
        dataTransfer[3] = new LatLng ( destination.latitude, destination.longitude );
        getDirectionsData.execute ( dataTransfer );
        return true;
    }
    /*****************User Location Functions *******************/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest ( ).create ( );//
        locationRequest.setPriority ( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval ( 50 );

        if (ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder ( )
                .addLocationRequest ( locationRequest );
        builder.setAlwaysShow ( true );
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings (
                        client,
                        builder.build ( )
                );
        result.setResultCallback (this);
        LocationServices.FusedLocationApi.requestLocationUpdates ( client,locationRequest,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates ( client, this);
        if (location == null) {
             progressDialog.dismiss();
             currentLocation=testLatLng;

            PushDataOnMap();
            Toast.makeText ( getApplicationContext ( ), "Your location could not be found", Toast.LENGTH_SHORT ).show ( );
        } else {
            currentLocation = new LatLng ( location.getLatitude ( ), location.getLongitude ( ) );
          currentLocation=testLatLng;
           drawCurrentLocationMarker();
               progressDialog.dismiss();
            PushDataOnMap();
                getUserInfo();
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

                    status.startResolutionForResult ( GoogleMapNavigation.this, 202 );

                } catch (IntentSender.SendIntentException e) {

                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }
    private  void ImageViewRounded(Bitmap profileBtm){
        profileBtm = bitmapUtility.getCircularBitmap(profileBtm);
        profileBtm = bitmapUtility.addBorderToCircularBitmap(profileBtm, 30, getResources().getColor(R.color.buttonColors));
        profileImage.setImageBitmap(profileBtm);
    }
}

