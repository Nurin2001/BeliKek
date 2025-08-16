package com.example.belikek;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class MenuItem implements Parcelable {
    public String id;        // key Firestore (cth: "baby_shark")
    public String name;      // value Firestore (cth: "Baby Shark")
    public double price;     // harga (boleh set default)
    public String imagePath; // path gambar dalam Firebase Storage
    boolean hasCakeOptions;

    public MenuItem(String id, String name, double price, String imagePath, boolean hasCakeOptions) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.hasCakeOptions = hasCakeOptions;
    }

    // parcleable constructor
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected MenuItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readDouble();
        imagePath = in.readString();
        hasCakeOptions = in.readBoolean();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(imagePath);
        dest.writeBoolean(hasCakeOptions);
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isHasCakeOptions() {
        return hasCakeOptions;
    }

    public void setHasCakeOptions(boolean hasCakeOptions) {
        this.hasCakeOptions = hasCakeOptions;
    }
}
