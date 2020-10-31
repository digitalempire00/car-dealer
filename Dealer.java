package com.canada.cardelar.application.Models;

public class Dealer {
  public   String Title,
            OwnerName,
            RegistrationNumber,
            Description,
            ContactNumber,
            CoverImageURL,
            ProfileImageUrl,
            Email,
            Password,

            Address,
            City,
            Rating;
 public   Double Lat,
          Lng;
    public  Dealer(String Title,
                   String OwnerName,
                   String  RegistrationNumber,
                   String Description,
                   String ContactNumber,
                   String CoverImageURL,
                   String  ProfileImageUrl,
                   String Email,
                   String Password,
                   Double Lat,
                   Double Lng,
                   String Address,
                   String City,
                   String Rating){


        this.Title=Title;
                this.OwnerName=OwnerName;
                this.RegistrationNumber=RegistrationNumber;
                        this.Description=Description;
                        this.ContactNumber=ContactNumber;
                        this.CoverImageURL=CoverImageURL;
                        this.ProfileImageUrl=ProfileImageUrl;
                        this.Email=Email;
                        this.Password=Password;
                        this.Lat=Lat;
                        this.Lng=Lng;
                        this.Address=Address;
                        this.City=City;
                        this.Rating=Rating;


    }


}
