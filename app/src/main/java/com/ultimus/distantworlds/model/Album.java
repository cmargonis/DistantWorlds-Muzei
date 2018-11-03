package com.ultimus.distantworlds.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ultimus on 29/3/2016.
 */
public class Album implements Parcelable {
    public String id;
    public String title;
    public String description;
    public int datetime;
    public String cover;
    public String link;
    public boolean nsfw;
    public int imagesCount;
    public ArrayList<Image> images;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeInt(this.datetime);
        dest.writeString(this.cover);
        dest.writeString(this.link);
        dest.writeByte(nsfw ? (byte) 1 : (byte) 0);
        dest.writeInt(this.imagesCount);
        dest.writeTypedList(images);
    }

    public Album() {
    }

    protected Album(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.datetime = in.readInt();
        this.cover = in.readString();
        this.link = in.readString();
        this.nsfw = in.readByte() != 0;
        this.imagesCount = in.readInt();
        in.readTypedList(this.images, Image.CREATOR);
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
