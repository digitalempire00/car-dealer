package com.canada.cardelar.application.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class SQLiteDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="locationn.db";
    private static final String LOCATIONS_TABLE="locations_table";
    private static final String PLACES_TABLE="places_table";
    private static final int DATABASE_Version = 1;



    private static  final  String LOCATION_ID="location_id";
    private static  final  String LATITUDE="latitude";
    private static  final  String LONGITUDE="longitude";

    private static  final  String      database_place_id="database_place_id";
    private static  final  String      administrative_area_level_1="administrative_area_level_1";
    private static  final  String      administrative_area_level_2="administrative_area_level_2";
    private static  final  String      business_status="business_status";
    private static  final  String      compound_code="compound_code";
    private static  final  String      country="country";
    private static  final  String      formatted_address="formatted_address";
    private static  final  String      global_code="global_code";
    private static  final  String      international_phone_number="international_phone_number";
    private static  final  String      latitude=" latitude";
    private static  final  String      locality="locality";
    private static  final  String     longitude="longitude";
    private static  final  String      name="name";
    private static  final  String      opening_time="opening_time";
    private static  final  String      photo_reference="photo_reference";
    private static  final  String      photo_url="photo_url";
    private static  final  String      place_id=" place_id";
    private static  final  String      postal_code="postal_code";
    private static  final  String      rating="rating";
    private static  final  String      route="route";
    private static  final  String      street_number="street_number";
    private static  final  String      url="url";
    private static  final  String      user_ratings_total="user_ratings_total";
    private static  final  String      vicinity="vicinity";
    private static  final  String      website="website";

    public String CREATE_PLACES_TABLE = "CREATE TABLE " + PLACES_TABLE + "("
            + database_place_id + "  INTEGER PRIMARY KEY AUTOINCREMENT, "//0
            + administrative_area_level_2+ " TEXT, "//1
            + administrative_area_level_1+ " TEXT, "//2
            +business_status+ " TEXT, "//3
            +compound_code+ " TEXT, "//4
            +country+ " TEXT, "//5
            +formatted_address+ " TEXT, "//6
            +global_code+ " TEXT, "//7
            +international_phone_number+ " TEXT, "//8
            +latitude+ " TEXT, "//9
            +locality+ " TEXT, "//10
            +longitude+ " TEXT, "//11
            +name+ " TEXT, "//12
            +opening_time+ " TEXT, "//13
            +photo_reference+ " TEXT, "//14
            +photo_url+ " TEXT, "//15
            +place_id+ " TEXT, "//16
            +postal_code+ " TEXT, "//17
            +rating+ " TEXT, "//18
            +route+ " TEXT, "//19
            +street_number+ " TEXT, "//20
            +url+ " TEXT, "//21
            +user_ratings_total+ " TEXT, "//22
            +vicinity+ " TEXT, "//23
            + website+ " TEXT )";//24
    private static final String DROP_PLACES_TABLE ="DROP TABLE IF EXISTS "+PLACES_TABLE;

    public String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + LOCATIONS_TABLE + "("
            + LOCATION_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LATITUDE+ " TEXT, "
            + LONGITUDE+ " TEXT )";

    private static final String DROP_LOCATIONS_TABLE ="DROP TABLE IF EXISTS "+LOCATIONS_TABLE;
    public SQLiteDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATIONS_TABLE);
        db.execSQL(CREATE_PLACES_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_LOCATIONS_TABLE);
        db.execSQL(DROP_PLACES_TABLE);
        onCreate(db);
    }

public boolean InsertLocation(String latitude,
                              String longitude){
    SQLiteDatabase db=this.getWritableDatabase();
    ContentValues locationValues= new ContentValues();
    locationValues.put(LATITUDE,latitude);
    locationValues.put(LONGITUDE,longitude);
    long result=db.insert(LOCATIONS_TABLE,null,locationValues);
    db.close();
    return result != -1;
    }
    public boolean InsertPlace(String Administrative_area_level_2,
                               String   Administrative_area_level_1,
                               String      Business_status,
                               String    Compound_code,
                               String     Country,
                               String     Formatted_address,
                               String    Global_code,
                               String    International_phone_number,
                               String     Latitude,
                               String    Locality,
                               String    Longitude,
                               String     Name,
                               String     Opening_time,
                               String     Photo_reference,
                               String     Photo_url,
                               String     Place_id,
                               String     Postal_code,
                               String      Rating,
                               String      Route,
                               String      Street_number,
                               String     Url,
                               String      User_ratings_total,
                               String      Vicinity,
                               String      Website){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues locationValues= new ContentValues();
        locationValues.put(administrative_area_level_2,Administrative_area_level_2);
        locationValues.put(administrative_area_level_1,Administrative_area_level_1);
        locationValues.put(business_status, Business_status);
        locationValues.put(compound_code, Compound_code);
        locationValues.put(country, Country);
        locationValues.put(formatted_address, Formatted_address);
        locationValues.put(global_code, Global_code);
        locationValues.put(international_phone_number, International_phone_number);
        locationValues.put(latitude, Latitude);
        locationValues.put(locality , Locality);
        locationValues.put(longitude ,Longitude);
        locationValues.put(name, Name);
        locationValues.put(opening_time, Opening_time);
        locationValues.put(photo_reference,  Photo_reference);
        locationValues.put(photo_url,  Photo_url);
        locationValues.put(place_id, Place_id);
        locationValues.put(postal_code, Postal_code);
        locationValues.put(rating, Rating);
        locationValues.put( route, Route);
        locationValues.put(street_number, Street_number);
        locationValues.put(url,  Url);
        locationValues.put( user_ratings_total, User_ratings_total);
        locationValues.put( vicinity,  Vicinity);
        locationValues.put( website, Website);
        long result=db.insert(PLACES_TABLE,null,locationValues);
        db.close();
        return result != -1;
    }

    public int countPlaces(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursorLocation=db.rawQuery(" select * from "+PLACES_TABLE,null );

        cursorLocation.moveToFirst();
        return cursorLocation.getCount();
    }
    public Cursor getAllPlaces(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursorLocation=db.rawQuery(" select * from "+PLACES_TABLE,null );
        return cursorLocation;
    }
    public String getPlaceID(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursorLocation=db.rawQuery(" select * from "+PLACES_TABLE,null );
       if(cursorLocation.moveToFirst()){
        return    cursorLocation.getString(16);

       }else return "0";


    }

    public void deletePlace(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(PLACES_TABLE, database_place_id+"="+id, null);
        db.close();
    }
    public Cursor getAllLocations(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursorLocation=db.rawQuery(" select * from "+LOCATIONS_TABLE,null );
        return cursorLocation;
    }
    public  Cursor getSinglePlace(String PlaceID){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(" select * from "+ PLACES_TABLE +" where " + place_id+"='"+ PlaceID +"'",null );
        return cursor;

    }
    public  String  WithoutImageCount(){
        String h="";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(" select * from "+PLACES_TABLE,null );
       cursor.moveToFirst();
       int counter=0;
       int totalt=0;
       while (cursor.moveToNext()){
           totalt++;
           if (cursor.getString(15).equals("")){
               counter++;
           }
       }
       String x="";
       x=x+"Total dealer : " +String.valueOf(totalt) + " dealer without images : "+String.valueOf(counter);
return  x;

    }

}