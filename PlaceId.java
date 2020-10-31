package com.canada.cardelar.application.PlacesSearch;

public class PlaceId {
  public   String PlaceID,
    PlaceName,
    Vicinity,
    Latitude,
     Longitude;


   public PlaceId( String placeID,
            String vicinity,
            String latitude,
            String longitude
            ,String placeName){

       this.PlaceID=placeID;
       this.PlaceName=placeName;
       this.Vicinity=vicinity;
       this.Latitude=latitude;
       this.Longitude=longitude;


    }
}
