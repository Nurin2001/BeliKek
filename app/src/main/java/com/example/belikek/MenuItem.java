package com.example.belikek;

public class MenuItem {
    public String id;        // key Firestore (cth: "baby_shark")
    public String name;      // value Firestore (cth: "Baby Shark")
    public double price;     // harga (boleh set default)
    public String imagePath; // path gambar dalam Firebase Storage

    public MenuItem(String id, String name, double price, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

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
}
