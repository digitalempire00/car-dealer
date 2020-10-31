package com.canada.cardelar.application;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.canada.cardelar.application.DataBase.SQLiteDataBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SplashScreen extends AppCompatActivity {
SQLiteDataBase sqLiteDataBase=new SQLiteDataBase(this);
    FirebaseAuth auth;//Firebase Authentication
    FirebaseUser user;//Firebase User Object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        auth = FirebaseAuth.getInstance();//initialize Firebase Authentication object
        user = auth.getCurrentUser();//get instance for current user


        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    if(sqLiteDataBase.countPlaces()<=0){
                        downloadData();
                    }
                    sleep(3000);
                    if( user!=null)  { //if user already login
                        startActivity(new Intent(SplashScreen.this,  GoogleMapNavigation.class));//jump to main Activity
                       finish();
                    }else {// if no login jump to user Login Activity
                     startActivity(new Intent(SplashScreen.this, MainActivity.class));
                      finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        myThread.start();
    }

    public void downloadData(){
        String json="";
        try {
            InputStream inputStream=getAssets().open("ontario_car_dealer_data.json");
            BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer ();
            String line="";
            while((line= bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }

            json=sb.toString();
            JSONObject jsonFile=new JSONObject(json);

            JSONObject rootObject=jsonFile.getJSONObject("ontario");

            JSONArray keys=rootObject.names();
            for (int i=0;i<keys.length();i++){
                JSONObject place= rootObject.getJSONObject(keys.getString(i));
    sqLiteDataBase.InsertPlace(
            place.getString("administrative_area_level_2"),
            place.getString("administrative_area_level_1"),
            place.getString("business_status"),
            place.getString("compound_code"),
            place.getString("country"),
            place.getString("formatted_address"),
            place.getString("global_code"),
            place.getString("international_phone_number"),
            place.getString("latitude"),
            place.getString("locality"),
            place.getString("longitude"),
            place.getString("name"),
            place.getString("opening_time"),
            "no need",
            place.getString("photo_url"),
            place.getString("place_id"),
            place.getString("postal_code"),
            place.getString("rating"),
            place.getString("route"),
            place.getString("street_number"),
            place.getString("url"),
            place.getString("user_ratings_total"),
            place.getString("vicinity"),
            place.getString("website"));
            }
            Toast.makeText(getApplicationContext(),
                    String.valueOf(keys.length()),
                    Toast.LENGTH_SHORT)
                    .show();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}
