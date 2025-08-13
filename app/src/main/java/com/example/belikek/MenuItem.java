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
}
