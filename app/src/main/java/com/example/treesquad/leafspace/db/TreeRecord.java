package com.example.treesquad.leafspace.db;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class TreeRecord implements Parcelable {
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

    public TreeRecord(Parcel in) {
        this.location = new GeoPoint(in.readDouble(), in.readDouble());
        this.created = new Date(in.readLong());
        if (in.readByte() != 0) {
            this.image = Bitmap.CREATOR.createFromParcel(in);
        }
        this.imageName = in.readString();
        this.id = in.readString();
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

}
