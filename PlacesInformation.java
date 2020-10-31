package com.canada.cardelar.application.Models;

public class PlacesInformation {
  public String place_id,
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

    postal_code;

 public    PlacesInformation( String place_id,
                              String  latitude,
                              String  longitude,
                              String  name,
                              String  vicinity,
                              String  street_number,
                              String   route,
                              String   locality,
                              String administrative_area_level_2,
                              String   administrative_area_level_1,
                              String    country,
                              String   business_status,
                              String     formatted_address,
                              String  international_phone_number,
                              String     opening_time,
                              String   photo_reference,
                              String   compound_code,
                              String   global_code,
                              String    rating,
                              String    url,
                              String   user_ratings_total,
                              String    website,
                              String   photo_url,
                              String postal_code){





             this.place_id=place_id;
             this.latitude=latitude;
             this.longitude=longitude;
             this.name=name;
             this.vicinity=vicinity;
             this.street_number=street_number;
             this. route=route;
             this. locality=locality;
             this. administrative_area_level_2=administrative_area_level_2;
             this. administrative_area_level_1=administrative_area_level_1;
             this. country=country;
             this. business_status=business_status;
             this. formatted_address=formatted_address;

             this. international_phone_number=international_phone_number;
             this. opening_time=opening_time;
             this.photo_reference=photo_reference;

            this.compound_code=compound_code;
            this.global_code=global_code;
            this.rating=rating;
             this.url=url;
            this.user_ratings_total=user_ratings_total;
            this.website=website;
             this.photo_url=photo_url;
             this.postal_code=postal_code;


 }

}
