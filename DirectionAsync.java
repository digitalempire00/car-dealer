package com.canada.cardelar.application;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectionAsync extends AsyncTask<Object, String, String>
{


static Polyline[] polylines;
    static  int Total=0;

    HttpURLConnection httpURLConnection=null;
    String data = "";
    InputStream inputStream = null;

    GoogleMap mMap;
    String myurl;
    LatLng startLatLng,endLatLng;

    Context c;




   public DirectionAsync(Context c)
    {
        this.c=c;
    }

    @Override
    protected String doInBackground(Object... params) {



        mMap = (GoogleMap)params[0];
        myurl = ( String )params[1];
        startLatLng = (LatLng)params[2];
        endLatLng = (LatLng)params[3];
        try
        {
            URL url = new URL (myurl);

            httpURLConnection = ( HttpURLConnection )url.openConnection();
            httpURLConnection.connect();//connect

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream)); //start reading routes
            StringBuffer sb = new StringBuffer ();
            String line="";
            while((line= bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            data = sb.toString();
            bufferedReader.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {



           for (int i=0;i<Total;i++){

               polylines[i].remove ();
           }





       try {
            JSONObject jsonObject = new JSONObject (s);
            JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0).getJSONArray("steps");



            JSONArray jsonRoute = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");


            JSONObject jsonObject1 = jsonRoute.getJSONObject(0);





            int count = jsonArray.length();
            String[] polyline_array = new String[count];

            JSONObject jsonobject2;


            for (int i = 0; i < count; i++) {
                jsonobject2 = jsonArray.getJSONObject(i);
                String polygone = jsonobject2.getJSONObject("polyline").getString("points");

                polyline_array[i] = polygone;
            }

            int count2 = polyline_array.length;


            polylines=new Polyline[count2];
            Total=count2;
            for (int i = 0; i < count2; i++) {

                PolylineOptions options2 = new PolylineOptions();

                options2.color( Color.BLUE);
                options2.width(15);
                options2.addAll( PolyUtil.decode(polyline_array[i]));//decode polyline
                polylines[i]=   mMap.addPolyline(options2);

            }

        }catch(JSONException e)
        {
            e.printStackTrace();
        }


    }


}
