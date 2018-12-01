package com.example.treesquad.leafspace.db;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TreeRecord implements Parcelable {
    public GeoPoint location;
    public Date created;
    public Bitmap image;
    public String imageName;
    public String id;
    public String species;
    public double height;
    public double diameter;
    public int age;
    public TreeHealth health;
    public int lifeExpectancy;
    public String recommendations;

    public enum TreeHealth {
       HEALTHY,
       DISEASED,
       DEAD,
    }

    public TreeRecord(GeoPoint location, Bitmap image, Date created, String imageName, String id) {
        this.location = location;
        this.created = created;
        this.image = image;
        this.imageName = imageName;
        this.id = id;
    }

    public TreeRecord(DocumentSnapshot doc) {
        this.created = (Date) doc.get("created");
        this.imageName = (String) doc.get("image");
        this.location  = (GeoPoint) doc.get("location");
        this.id = doc.getId();

        if (doc.contains("species")) this.species = (String) doc.get("species");
        if (doc.contains("height")) this.height = (double) doc.get("height");
        if (doc.contains("diameter")) this.diameter = (double) doc.get("diameter");
        if (doc.contains("age")) this.age = (int) doc.get("age");
        if (doc.contains("health")) this.health = TreeHealth.values()[(int) doc.get("health")];
        if (doc.contains("life_expectancy")) this.lifeExpectancy = (int) doc.get("life_expectancy");
        if (doc.contains("recommendations")) this.recommendations = (String) doc.get("recommendations");
    }

    private TreeRecord(Builder builder) {
        location = builder.location;
        created = builder.created;
        image = builder.image;
        imageName = builder.imageName;
        id = builder.id;
        species = builder.species;
        height = builder.height;
        diameter = builder.diameter;
        age = builder.age;
        health = builder.health;
        lifeExpectancy = builder.lifeExpectancy;
        recommendations = builder.recommendations;
    }

    private TreeRecord(Parcel in) {
        this.location = new GeoPoint(in.readDouble(), in.readDouble());
        this.created = new Date(in.readLong());
        if (in.readByte() != 0) {
            this.image = Bitmap.CREATOR.createFromParcel(in);
        }
        this.imageName = in.readString();
        this.id = in.readString();
        this.species = in.readString();
        this.height = in.readDouble();
        this.diameter = in.readDouble();
        this.age = in.readInt();
        if (in.readByte() != 0) {
            this.health = TreeHealth.values()[in.readInt()];
        }
        this.lifeExpectancy = in.readInt();
        this.recommendations = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(location.getLatitude());
        out.writeDouble(location.getLongitude());
        out.writeLong(created.getTime());
        if (image != null) {
            out.writeByte((byte) 1);
            image.writeToParcel(out, flags);
        } else {
            out.writeByte((byte) 0);
        }
        out.writeString(imageName);
        out.writeString(id);
        out.writeString(species);
        out.writeDouble(height);
        out.writeDouble(diameter);
        out.writeInt(age);
        if(health != null) {
            out.writeByte((byte) 1);
            out.writeInt(health.ordinal());
        } else {
            out.writeByte((byte) 0);
        }
        out.writeInt(lifeExpectancy);
        out.writeString(recommendations);
    }

    public Map<String,Object> toMap() {
        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("created", created);
        recordMap.put("image", imageName);
        recordMap.put("location", location);

        if (species != null) recordMap.put("species", species);
        if (height != 0) recordMap.put("height", height);
        if (diameter != 0) recordMap.put("diameter", diameter);
        if (age != 0) recordMap.put("age", age);
        if (health != null) recordMap.put("health", health.ordinal());
        if (lifeExpectancy != 0) recordMap.put("life_expectancy", lifeExpectancy);
        if (recommendations != null) recordMap.put("recommendations", recommendations);

        return recordMap;
    }

    @Override
    public int describeContents(){
       return 0;
    }

    public static final Parcelable.Creator<TreeRecord> CREATOR = new Parcelable.Creator<TreeRecord>() {
        @Override
        public TreeRecord createFromParcel(Parcel in) {
            return new TreeRecord(in);
        }

        @Override
        public TreeRecord[] newArray(int size) {
            return new TreeRecord[size];
        }
    };

    public static class Builder {
        private GeoPoint location;
        private Date created;
        private Bitmap image;
        private String imageName;
        private String id;
        private String species;
        private double height;
        private double diameter;
        private int age;
        private TreeHealth health;
        private int lifeExpectancy;
        private String recommendations;
        private String user;

        public Builder(GeoPoint location) {
            this.location = location;
        }

        public Builder image(Bitmap image) {
            this.image = image;
            return this;
        }

        protected Builder created(Date created) {
            this.created = created;
            return this;
        }

        public Builder imageName(String imageName) {
            this.imageName = imageName;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder species(String species) {
            this.species = species;
            return this;
        }

        public Builder height(double height) {
            this.height = height;
            return this;
        }

        public Builder diameter(double diameter) {
            this.diameter = diameter;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder health(TreeHealth health) {
            this.health = health;
            return this;
        }

        public Builder lifeExpectancy(int lifeExpectancy) {
            this.lifeExpectancy = lifeExpectancy;
            return this;
        }

        public Builder recommendations(String recommendations) {
            this.recommendations = recommendations;
            return this;
        }

        public Builder user() {
            this.user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            return this;
        }

        public TreeRecord build() {
            if (created == null) created(new Date());
            return new TreeRecord(this);
        }
    }
}
