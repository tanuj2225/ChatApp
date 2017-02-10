package edu.uncc.tanuj.chatapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinay on 11/18/2016.
 */

public class User implements Serializable {
    String email,firstname,lastname,profilepicUri,userKey,gender="Male",status="no status";

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User(String email, String firstname, String profilepicUri, String lastname, String userKey) {
        this.email = email;
        this.firstname = firstname;
        this.profilepicUri = profilepicUri;
        this.lastname = lastname;
        this.userKey = userKey;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", profilepicUri='" + profilepicUri + '\'' +
                ", userKey='" + userKey + '\'' +
                '}';
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getProfilepicUri() {
        return profilepicUri;
    }

    public void setProfilepicUri(String profilepicUri) {
        this.profilepicUri = profilepicUri;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstname", firstname);
        result.put("lastname", lastname);
        result.put("email", email);
        result.put("profilepicUri", profilepicUri);
        result.put("userKey", userKey);
        result.put("status", status);
        result.put("gender", gender);
        return result;
    }
}
