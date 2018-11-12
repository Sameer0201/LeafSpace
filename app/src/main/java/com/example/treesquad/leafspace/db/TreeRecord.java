package com.example.treesquad.leafspace.db;

import android.graphics.Bitmap;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class TreeRecord {
    public GeoPoint location;
    public Date created;
    public Bitmap image;
    public String imageName;
    public String id;

    public TreeRecord(GeoPoint location, Bitmap image, Date created, String imageName, String id) {
        this.location = location;
        this.created = created;
        this.image = image;
        this.imageName = imageName;
        this.id = id;
    }
}
