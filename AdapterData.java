package com.canada.cardelar.application.Models;
import android.graphics.Bitmap;

public class AdapterData {
    String dealerName;
    String distanceAway;

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    String ratting;
    String totalRatting;
    Bitmap dealerImage;
    String imageURL;
    String placeId;

    public AdapterData(String dealerName,
                       String distanceAway,
                       String ratting,
                       String totalRatting,
                       Bitmap dealerImage,
                       String imageURL,
                       String placeId) {
        this.dealerName=dealerName;
        this.distanceAway=distanceAway;
        this.imageURL=imageURL;
        this.ratting=ratting;
        this.totalRatting=totalRatting;
        this.dealerImage=dealerImage;
        this.placeId=placeId;
    }
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }
    public void setDistanceAway(String distanceAway) {
        this.distanceAway = distanceAway;
    }
    public void setRatting(String ratting) {
        this.ratting = ratting;
    }
    public void setTotalRatting(String totalRatting) {
        this.totalRatting = totalRatting;
    }
    public void setDealerImage(Bitmap dealerImage) {
        this.dealerImage = dealerImage;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getDealerName() {
        return dealerName;
    }
    public String getDistanceAway() {
        return distanceAway;
    }
    public String getRatting() {
        return ratting;
    }
    public String getTotalRatting() {
        return totalRatting;
    }
    public Bitmap getDealerImage() {
        return dealerImage;
    }
    public String getImageURL() {
        return imageURL;
    }
}
