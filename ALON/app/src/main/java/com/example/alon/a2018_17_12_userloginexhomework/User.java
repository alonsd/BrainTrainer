package com.example.alon.a2018_17_12_userloginexhomework;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class User {

    private final char SEPRATOR = ';';
    private String username, password;
    private String profilePicturePath;

    public User(String username, String password, String profilePicturePath) {
        this.username = username;
        this.password = password;
        this.profilePicturePath = profilePicturePath;
    }

    public User(String s) {
        StringBuilder username = new StringBuilder();
        StringBuilder password = new StringBuilder();
        int separatorPlace = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == SEPRATOR) {
                separatorPlace = i;
                break;
            }
            username.append(s.charAt(i));
        }
        for (int i = separatorPlace+1; i < s.length(); i++) {
            password.append(s.charAt(i));
        }
        this.username = username.toString();
        this.password = password.toString();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    @Override
    public String toString() {
        return username + SEPRATOR + password;
    }

}
