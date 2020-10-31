package com.canada.cardelar.application.Models;

public class User {
    public String Name,
            Email,
            Password,
            Phone,
            userProfileImage;

    public User(String name,
                String email,
                String phone,
               String password,
                String userProfileImage) {
        this.Name = name;
        this.Email = email;
        this.Password = password;
        this.Phone=phone;
        this.userProfileImage=userProfileImage;
    }
}
