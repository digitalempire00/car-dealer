package com.canada.cardelar.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.canada.cardelar.application.DataBase.SQLiteDataBase;
import com.canada.cardelar.application.Models.BitmapUtility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PlaceDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    SQLiteDataBase sqLiteDataBase = new SQLiteDataBase(this);
    BitmapUtility bitmapUtility = new BitmapUtility();
    String placeId;
    ImageView arrowBackImageView, shareImageView, dealerImageView;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    TextView placeNameTextView, rattingTextView, totalRattingTextView,
            countryTextView, AdmLevelOneTextView, AdmLevelTwoTextView, formattedAddressTextView, routeTextView, streetNumberTextView, VicinityTextView, postalCodeTextView,
            businessStatusTextView, compoundCodeTextView, globalCodeTextView,
            contactNumberTextView, emailTextView,
            openingTimeTextView,
            userCommentsTextView;

    LinearLayout linearLayoutWebSite, linearLayoutMapUrl, linearLayoutCars, linearLayoutRateDealer;
    ImageView[] star = new ImageView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        initView();
        Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.simple_placeholder);
        dealerImageView.setImageBitmap(bitmapUtility.setTopRounded(placeholder, placeholder.getWidth(), placeholder.getHeight()));
        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        getPlaceData();
    }

    @SuppressLint("SetTextI18n")
    private void getPlaceData() {
        Cursor cursor = sqLiteDataBase.getSinglePlace(placeId);
        if (cursor.moveToNext()) {
            if (cursor.getString(15).equals("")) {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_place);
                dealerImageView.setImageBitmap(bitmapUtility.setTopRounded(placeholder, placeholder.getWidth(), placeholder.getHeight()));

            } else {
                downloadImage(cursor.getString(15));
            }
            placeNameTextView.setText(cursor.getString(12));
            countryTextView.setText("Country : " + cursor.getString(5));
            AdmLevelOneTextView.setText("Administrative area level 1 : " + cursor.getString(2));
            AdmLevelTwoTextView.setText("Administrative area level 2 : " + cursor.getString(1));
            formattedAddressTextView.setText("Address : " + cursor.getString(6));
            routeTextView.setText("Route : " + cursor.getString(19));
            streetNumberTextView.setText("Street Number : " + cursor.getString(20));
            VicinityTextView.setText("Vicinity : " + cursor.getString(23));
            postalCodeTextView.setText("Postal Code : " + cursor.getString(17));
            businessStatusTextView.setText("Businees Status : " + cursor.getString(3));
            compoundCodeTextView.setText("Compound Code : " + cursor.getString(4));
            globalCodeTextView.setText("Global Code : " + cursor.getString(7));
            contactNumberTextView.setText("Contact Number : " + cursor.getString(8));
            emailTextView.setText("NA");
            linearLayoutWebSite.setTag(cursor.getString(24));
            linearLayoutMapUrl.setTag(cursor.getString(21));


            if (cursor.getString(13).equals("")) {
                openingTimeTextView.setText("NA");
            } else
                openingTimeTextView.setText(cursor.getString(13));
            userCommentsTextView.setText("NA");
            if (cursor.getString(18).equals("")) {
                rattingTextView.setText("Ratting : ");
                totalRattingTextView.setText("NA");
            } else {
                rattingTextView.setText(cursor.getString(18));
                totalRattingTextView.setText("(" + cursor.getString(22) + ")");
                Double dRate = cursor.getDouble(18);
                int Irate = dRate.intValue();
                double point = dRate - Irate;

                int totalStar = 5;
                int i = 0;
                for (; i < Irate; i++) {
                    star[i].setImageResource(R.drawable.ic_full_star);
                    totalStar--;
                }
                if (totalStar != 0) {
                    if (point >= 0.3) {
                        star[i].setImageResource(R.drawable.ic_half_star);
                        i++;
                        totalStar--;
                    }
                }
                for (; i < 5; i++) {
                    star[i].setImageResource(R.drawable.ic_empty_star);
                }
            }
        } else {
            //handle error
        }
    }

    private void downloadImage(String Url) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        StorageReference imageReference = storageReference.child(Url);
        long MAXBYTS = 1024 * 1024;
        imageReference.getBytes(MAXBYTS).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap mbitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                dealerImageView.setImageBitmap(bitmapUtility.setTopRounded(mbitmap, mbitmap.getWidth(), mbitmap.getHeight()));
            }
        });
    }

    private void initView() {
        arrowBackImageView = findViewById(R.id.backArrow);
        shareImageView = findViewById(R.id.imageViewShare);
        dealerImageView = findViewById(R.id.imageViewDealerProfileImage);
        star[0] = findViewById(R.id.imageViewStar1);
        star[1] = findViewById(R.id.imageViewStar2);
        star[2] = findViewById(R.id.imageViewStar3);
        star[3] = findViewById(R.id.imageViewStar4);
        star[4] = findViewById(R.id.imageViewStar5);
        placeNameTextView = findViewById(R.id.textViewShopName);
        rattingTextView = findViewById(R.id.textViewRatting);
        totalRattingTextView = findViewById(R.id.textViewTotal);

        countryTextView = findViewById(R.id.textViewCountry);
        AdmLevelOneTextView = findViewById(R.id.textViewAdmLevelOne);
        AdmLevelTwoTextView = findViewById(R.id.textViewAdmLevelTwo);
        formattedAddressTextView = findViewById(R.id.textViewFormattedAddress);
        routeTextView = findViewById(R.id.textViewRoute);
        streetNumberTextView = findViewById(R.id.textViewStreetNumber);
        VicinityTextView = findViewById(R.id.textViewVicinity);
        postalCodeTextView = findViewById(R.id.textViewPostalCode);

        businessStatusTextView = findViewById(R.id.textViewBusinessStatus);
        compoundCodeTextView = findViewById(R.id.textViewCompoundCode);
        globalCodeTextView = findViewById(R.id.textViewGlobalCode);

        contactNumberTextView = findViewById(R.id.textViewContactNumber);
        emailTextView = findViewById(R.id.textViewEmail);

        openingTimeTextView = findViewById(R.id.textViewOpeningTime);

        userCommentsTextView = findViewById(R.id.textViewUserComments);

        linearLayoutCars = findViewById(R.id.layoutCars);
        linearLayoutMapUrl = findViewById(R.id.layoutMapUrl);
        linearLayoutRateDealer = findViewById(R.id.LayoutRateDealer);
        linearLayoutWebSite = findViewById(R.id.layoutWebSite);

        linearLayoutWebSite.setOnClickListener(this);
        linearLayoutRateDealer.setOnClickListener(this);
        linearLayoutMapUrl.setOnClickListener(this);
        linearLayoutCars.setOnClickListener(this);
        arrowBackImageView.setOnClickListener(this);
        shareImageView.setOnClickListener(this);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.backArrow:
                finish();
                break;
            case R.id.imageViewShare:
                showToast("UnderConstruction");
                break;
            case R.id.layoutCars:
                showToast("cars data will need time We will Upload it in future ");
                break;
            case R.id.layoutMapUrl:
                if (linearLayoutMapUrl.getTag().toString().equals("")) {
                    showToast("No Web Site Found For this car dealer");
                } else{
                    Intent intent=new Intent(PlaceDetailsActivity.this,UrlLoader.class);
                    intent.putExtra("url",linearLayoutMapUrl.getTag().toString());
                    startActivity(intent);
                }
                break;
            case R.id.layoutWebSite:
                if (linearLayoutWebSite.getTag().toString().equals("")) {
                    showToast("No Web Site Found For this car dealer");
                } else{
                    Intent intent=new Intent(PlaceDetailsActivity.this,UrlLoader.class);
                    intent.putExtra("url",linearLayoutWebSite.getTag().toString());
                    startActivity(intent);
                }
                break;
            case R.id.LayoutRateDealer:
                //open Ratting Dialog
                break;
            default:
                showToast("Something Going Wrung Please Restart Application");
                break;
        }
    }
}





