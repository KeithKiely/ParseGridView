package com.parse.starter;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Images")
public class Image extends ParseObject {

    public Image() {
        // A default constructor is required.
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public String getRating(String s) {
        return getString("rating");
    }

    public void setRating(String rating) {
        put("rating", rating);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("images");
    }

    public void setPhotoFile(ParseFile file) {
        put("images", file);
    }

    public void setPrivacyStatus(boolean status ) {
        put("private_image", status);
    }

    public boolean getPrivacyStatus() {
        return getBoolean("private_image");
    }

}