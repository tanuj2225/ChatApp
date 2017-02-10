package edu.uncc.tanuj.chatapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinay on 11/20/2016.
 */

public class Album {
    String imageUri;
    Date date;

    @Override
    public String toString() {
        return "Album{" +
                "imageUri='" + imageUri + '\'' +
                ", date=" + date +
                ", userID='" + userID + '\'' +
                ", imageKey='" + imageKey + '\'' +
                '}';
    }

    String userID,imageKey;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("imageKey", imageKey);
        result.put("imageUri", imageUri);
        result.put("userID", userID);
        result.put("date", date);
        return result;
    }
}
