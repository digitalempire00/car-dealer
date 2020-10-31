package com.canada.cardelar.application.PlacesSearch;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import com.canada.cardelar.application.Models.PlacesInformation;
import com.canada.cardelar.application.PlacesSearch.TestingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//


public class PlacesDetails extends AsyncTask<Object, String, String>
{

    HttpURLConnection httpURLConnection=null;
    String googleMapDirectionsResponse = "";
    InputStream inputStream = null;
    String inputStringUrl;
    Context context;


    String place_id="";
    String latitude="";
    String longitude="";
    String name="";
    String vicinity;

   public PlacesDetails(Context context)
    {
        this.context=context;
    }

    @Override
    protected String doInBackground(Object... params) {
        inputStringUrl = ( String )params[0];
        place_id= ( String )params[3];
        latitude= ( String )params[1];
        longitude= ( String )params[2];
        name= ( String )params[4];
        vicinity= ( String )params[5];
        try
        {
            URL url = new URL (inputStringUrl);
            httpURLConnection = ( HttpURLConnection )url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
            StringBuffer sb = new StringBuffer ();
            String line="";
            while((line= bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            googleMapDirectionsResponse= sb.toString();
            bufferedReader.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return googleMapDirectionsResponse;
    }

    @Override
    protected void onPostExecute(String s) {
        String street_number="";
        String route="";
        String locality="";
        String administrative_area_level_2="";
        String administrative_area_level_1="";
        String country="";
        String business_status="";
        String formatted_address="";
        String icon="";
        String international_phone_number="";
        String opening_time="";
        String photo_reference="";

        String compound_code="";
        String global_code="";
        String rating="";
        String url="";
        String user_ratings_total="";
        String website="";
        String photo_url="";
        String icon_url="";
        String postalCode="";
        try {
            JSONObject jsonFile = new JSONObject (s);
            String status=jsonFile.getString("status");
            if(status.equals("OK")) {
                if(jsonFile.has("result")) {
                    JSONObject rootObject = jsonFile.getJSONObject("result");
                      if(rootObject.has("address_components")) {
                          JSONArray address_component = rootObject.getJSONArray("address_components");

                        if(address_component.length()>0) {
                            JSONObject jsonObjectStreet = address_component.getJSONObject(0);
                            street_number = jsonObjectStreet.getString("long_name");
                            if(address_component.length()>1) {
                                JSONObject jsonObjectRoute = address_component.getJSONObject(1);
                                route = jsonObjectRoute.getString("long_name");
                                if(address_component.length()>2) {
                                    JSONObject jsonObjectLocality = address_component.getJSONObject(2);
                                    locality = jsonObjectLocality.getString("long_name");
                                    if(address_component.length()>3) {
                                        JSONObject jsonObjectAdministrative_area_level_2 = address_component.getJSONObject(3);
                                        administrative_area_level_2 = jsonObjectAdministrative_area_level_2.getString("long_name");
                                        if(address_component.length()>4) {
                                            JSONObject jsonObjectAdministrative_area_level_1 = address_component.getJSONObject(4);
                                            administrative_area_level_1 = jsonObjectAdministrative_area_level_1.getString("long_name");
                                            if(address_component.length()>5) {
                                            JSONObject jsonObjectCountry = address_component.getJSONObject(5);
                                            country = jsonObjectCountry.getString("long_name");
                                                if(address_component.length()>6) {
                                                    JSONObject jsonObjectPostalCode = address_component.getJSONObject(6);
                                                    postalCode = jsonObjectPostalCode.getString("long_name");
                                                }
                                                }
                                        }
                                        }
                                    }
                                }
                        }
                      }
                    if(rootObject.has("business_status"))
                    business_status = rootObject.getString("business_status");
                    if(rootObject.has("formatted_address"))
                    formatted_address = rootObject.getString("formatted_address");
                    TestingActivity.textView.setText(street_number + "\n" + route + locality + "\n" + administrative_area_level_1 + "\n" + administrative_area_level_2 + "\n" + country + "\n" + postalCode + "\n" + business_status);
                    TestingActivity.textView.append("\n" + formatted_address);
                    if(rootObject.has("icon"))
                    icon = rootObject.getString("icon");
                    TestingActivity.textView.append("\n" + icon);
                    if(rootObject.has("international_phone_number"))
                    international_phone_number = rootObject.getString("international_phone_number");
                    TestingActivity.textView.append("\n" + international_phone_number);
                    if(rootObject.has("opening_hours")) {
                        JSONArray jsonObjectTime_Details = rootObject.getJSONObject("opening_hours").getJSONArray("weekday_text");
                        opening_time = "";
                        for (int i = 0; i < jsonObjectTime_Details.length(); i++) {
                            opening_time += jsonObjectTime_Details.getString(i) + "\n";

                        }
                        TestingActivity.textView.append("\n" + opening_time);
                    }
                    if(rootObject.has("photos")) {
                        JSONArray jsonArrayPhoto = rootObject.getJSONArray("photos");

                        for (int i=0;i<jsonArrayPhoto.length();i++){
                            if (photo_reference.equals("")){
                                photo_reference = jsonArrayPhoto.getJSONObject(i).getString("photo_reference");
                            }

                        }

                    }
                    if(rootObject.has("plus_code")) {
                        JSONObject jsonObjectPlus_code = rootObject.getJSONObject("plus_code");
                      if(  jsonObjectPlus_code.has("compound_code"))
                        compound_code = jsonObjectPlus_code.getString("compound_code");
                        if(  jsonObjectPlus_code.has("global_code"))
                        global_code = jsonObjectPlus_code.getString("global_code");
                    }
                    if(rootObject.has("rating"))
                    rating = rootObject.getString("rating");
                    if(rootObject.has("url"))
                    url = rootObject.getString("url");
                    if(rootObject.has("user_ratings_total"))
                    user_ratings_total = rootObject.getString("user_ratings_total");
                    if(rootObject.has("website"))
                    website = rootObject.getString("website");
                    TestingActivity.textView.append("\n" + compound_code + "\n" + global_code + "\n" + rating + "\n" + url + "\n" + user_ratings_total + "\n" + website + "\n");
                    TestingActivity.textView.append("\n" + photo_reference);

                }

            }


            PlacesInformation placesInformation=new PlacesInformation( place_id,
                    latitude,
                    longitude,
                    name,
                    vicinity,
                    street_number,
                    route,
                    locality,
                    administrative_area_level_2,
                    administrative_area_level_1,
                    country,
                    business_status,
                    formatted_address,
                    international_phone_number,
                    opening_time,
                    photo_reference,

                    compound_code,
                    global_code,
                    rating,
                    url,
                    user_ratings_total,
                    website,
                    photo_url,
                    postalCode);
         DatabaseReference   databaseReference = FirebaseDatabase.getInstance().
                    getReference().child("places_details").child("canada").child(place_id);

            databaseReference.setValue(placesInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        TestingActivity.textView.append("\n\n\nupload Done\n\n");
                        TestingActivity.textView.setTextColor(Color.RED);
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
