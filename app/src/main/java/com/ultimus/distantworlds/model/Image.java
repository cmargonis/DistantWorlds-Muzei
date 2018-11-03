package com.ultimus.distantworlds.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ultimus on 29/3/2016.
 */
public class Image implements Parcelable {
    public String id;
    public String title;
    public String description;
    public int datetime;
    public String type;
    public boolean animated;
    public int width;
    public int height;
    public int size;
    public int views;
    public double bandwidth;

    public String link;
    public boolean nsfw;


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
        dest.writeString(this.type);
        dest.writeByte(animated ? (byte) 1 : (byte) 0);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.size);
        dest.writeInt(this.views);
        dest.writeDouble(this.bandwidth);
        dest.writeString(this.link);
        dest.writeByte(nsfw ? (byte) 1 : (byte) 0);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.datetime = in.readInt();
        this.type = in.readString();
        this.animated = in.readByte() != 0;
        this.width = in.readInt();
        this.height = in.readInt();
        this.size = in.readInt();
        this.views = in.readInt();
        this.bandwidth = in.readDouble();
        this.link = in.readString();
        this.nsfw = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
