package com.example.belikek;

public class ProductUI {
    public final String id;
    public final String name;
    public final String imagePathOrUrl; // Storage path atau URL
    public final double price;

    public ProductUI(String id, String name, String imagePathOrUrl, double price) {
        this.id = id; this.name = name; this.imagePathOrUrl = imagePathOrUrl; this.price = price;
    }
}
